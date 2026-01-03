# Frontend Integration Guide - Bulk SMS to Registrations

## Quick Integration Steps

### 1. Create Registration List View

Add a table/list view showing event registrations with checkboxes:

```html
<div id="registrations-container">
    <table class="registrations-table">
        <thead>
            <tr>
                <th>
                    <input type="checkbox" id="select-all" />
                </th>
                <th>Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Registration Date</th>
            </tr>
        </thead>
        <tbody id="registrations-body">
            <!-- Populated dynamically -->
        </tbody>
    </table>
</div>
```

### 2. Load Registrations

```javascript
async function loadRegistrations(eventId) {
    try {
        const response = await fetch(`/api/v1/events/${eventId}/registrations?page=0&size=50`);
        const data = await response.json();
        
        const tbody = document.getElementById('registrations-body');
        tbody.innerHTML = '';
        
        data.content.forEach(registration => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>
                    <input type="checkbox" class="registration-checkbox" 
                           data-email="${registration.email}" />
                </td>
                <td>${registration.name}</td>
                <td>${registration.email}</td>
                <td>${registration.phone}</td>
                <td>${new Date(registration.createdAt).toLocaleDateString()}</td>
            `;
            tbody.appendChild(row);
        });
        
        setupSelectAll();
    } catch (error) {
        console.error('Failed to load registrations:', error);
        showError('Failed to load registrations');
    }
}
```

### 3. Handle Select All Checkbox

```javascript
function setupSelectAll() {
    const selectAllCheckbox = document.getElementById('select-all');
    const registrationCheckboxes = document.querySelectorAll('.registration-checkbox');
    
    selectAllCheckbox.addEventListener('change', (e) => {
        registrationCheckboxes.forEach(checkbox => {
            checkbox.checked = e.target.checked;
        });
    });
    
    registrationCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', () => {
            const allChecked = Array.from(registrationCheckboxes).every(c => c.checked);
            selectAllCheckbox.checked = allChecked;
        });
    });
}
```

### 4. SMS Modal/Form

```html
<div id="sms-modal" class="modal hidden">
    <div class="modal-content">
        <h2>Send Bulk SMS</h2>
        
        <div class="form-group">
            <label>Selected Registrations: <span id="selected-count">0</span></label>
        </div>
        
        <div class="form-group">
            <label for="sms-message">Message:</label>
            <textarea 
                id="sms-message" 
                placeholder="Enter your message (max 1600 characters)"
                rows="5"
                maxlength="1600"
            ></textarea>
            <small id="char-count">0/1600</small>
        </div>
        
        <div class="form-actions">
            <button id="send-sms-btn" class="btn btn-primary">Send SMS</button>
            <button id="cancel-sms-btn" class="btn btn-secondary">Cancel</button>
        </div>
        
        <div id="sms-status" class="status-message hidden"></div>
    </div>
</div>
```

### 5. Send Bulk SMS Function

```javascript
async function sendBulkSms(eventId) {
    const selectedCheckboxes = document.querySelectorAll('.registration-checkbox:checked');
    const selectedEmails = Array.from(selectedCheckboxes).map(cb => cb.dataset.email);
    const message = document.getElementById('sms-message').value;
    
    // Validation
    if (selectedEmails.length === 0) {
        showError('Please select at least one registration');
        return;
    }
    
    if (!message.trim()) {
        showError('Please enter a message');
        return;
    }
    
    if (message.length > 1600) {
        showError('Message must be 1600 characters or less');
        return;
    }
    
    try {
        showLoading('Sending SMS...');
        
        const response = await fetch(
            `/api/v1/events/${eventId}/registrations/send-sms`,
            {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${getAuthToken()}`
                },
                body: JSON.stringify({
                    selectedEmails: selectedEmails,
                    message: message
                })
            }
        );
        
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to send SMS');
        }
        
        const result = await response.json();
        
        showSuccess(`SMS sent successfully to ${selectedEmails.length} recipients`);
        closeModal('sms-modal');
        resetForm();
        
    } catch (error) {
        console.error('SMS sending failed:', error);
        showError(error.message);
    }
}
```

### 6. Character Count & Message Preview

```javascript
const messageInput = document.getElementById('sms-message');

messageInput.addEventListener('input', (e) => {
    const count = e.target.value.length;
    document.getElementById('char-count').textContent = `${count}/1600`;
    
    // Show warning if nearing limit
    if (count > 1500) {
        document.getElementById('char-count').classList.add('warning');
    } else {
        document.getElementById('char-count').classList.remove('warning');
    }
});
```

### 7. Event Listeners Setup

```javascript
function initializeSmsFeature(eventId) {
    // Load registrations
    loadRegistrations(eventId);
    
    // Open SMS modal
    document.getElementById('send-bulk-sms-btn').addEventListener('click', () => {
        const selectedCount = document.querySelectorAll('.registration-checkbox:checked').length;
        
        if (selectedCount === 0) {
            showError('Please select at least one registration');
            return;
        }
        
        document.getElementById('selected-count').textContent = selectedCount;
        openModal('sms-modal');
    });
    
    // Send SMS
    document.getElementById('send-sms-btn').addEventListener('click', () => {
        sendBulkSms(eventId);
    });
    
    // Cancel
    document.getElementById('cancel-sms-btn').addEventListener('click', () => {
        closeModal('sms-modal');
        resetForm();
    });
}
```

### 8. Helper Functions

```javascript
function showLoading(message) {
    const statusDiv = document.getElementById('sms-status');
    statusDiv.textContent = message;
    statusDiv.className = 'status-message loading';
    statusDiv.classList.remove('hidden');
}

function showSuccess(message) {
    const statusDiv = document.getElementById('sms-status');
    statusDiv.textContent = message;
    statusDiv.className = 'status-message success';
    statusDiv.classList.remove('hidden');
}

function showError(message) {
    const statusDiv = document.getElementById('sms-status');
    statusDiv.textContent = message;
    statusDiv.className = 'status-message error';
    statusDiv.classList.remove('hidden');
}

function openModal(modalId) {
    document.getElementById(modalId).classList.remove('hidden');
}

function closeModal(modalId) {
    document.getElementById(modalId).classList.add('hidden');
}

function resetForm() {
    document.getElementById('sms-message').value = '';
    document.getElementById('char-count').textContent = '0/1600';
    document.querySelectorAll('.registration-checkbox').forEach(cb => cb.checked = false);
    document.getElementById('select-all').checked = false;
}

function getAuthToken() {
    // Get from localStorage, sessionStorage, or cookie
    return localStorage.getItem('authToken');
}
```

### 9. Styling

```css
.registrations-table {
    width: 100%;
    border-collapse: collapse;
    margin: 20px 0;
}

.registrations-table th,
.registrations-table td {
    padding: 12px;
    text-align: left;
    border-bottom: 1px solid #ddd;
}

.registrations-table th {
    background-color: #f5f5f5;
    font-weight: bold;
}

.registrations-table tbody tr:hover {
    background-color: #f9f9f9;
}

.registrations-table input[type="checkbox"] {
    width: 18px;
    height: 18px;
    cursor: pointer;
}

.modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
}

.modal.hidden {
    display: none;
}

.modal-content {
    background: white;
    padding: 30px;
    border-radius: 8px;
    max-width: 500px;
    width: 90%;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.modal-content h2 {
    margin-top: 0;
    margin-bottom: 20px;
}

.form-group {
    margin-bottom: 15px;
}

.form-group label {
    display: block;
    margin-bottom: 5px;
    font-weight: bold;
}

.form-group textarea {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-family: Arial, sans-serif;
    resize: vertical;
}

.form-group small {
    display: block;
    margin-top: 5px;
    color: #666;
}

.form-group small.warning {
    color: #ff6b6b;
}

.form-actions {
    display: flex;
    gap: 10px;
    margin-top: 20px;
}

.btn {
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
    transition: background-color 0.3s;
}

.btn-primary {
    background-color: #007bff;
    color: white;
}

.btn-primary:hover {
    background-color: #0056b3;
}

.btn-secondary {
    background-color: #6c757d;
    color: white;
}

.btn-secondary:hover {
    background-color: #545b62;
}

.status-message {
    padding: 10px;
    margin-top: 15px;
    border-radius: 4px;
    display: none;
}

.status-message:not(.hidden) {
    display: block;
}

.status-message.success {
    background-color: #d4edda;
    color: #155724;
    border: 1px solid #c3e6cb;
}

.status-message.error {
    background-color: #f8d7da;
    color: #721c24;
    border: 1px solid #f5c6cb;
}

.status-message.loading {
    background-color: #d1ecf1;
    color: #0c5460;
    border: 1px solid #bee5eb;
}

#send-bulk-sms-btn {
    background-color: #28a745;
    color: white;
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

#send-bulk-sms-btn:hover {
    background-color: #218838;
}
```

### 10. Initialize on Page Load

```javascript
document.addEventListener('DOMContentLoaded', () => {
    const eventId = new URLSearchParams(window.location.search).get('eventId');
    if (eventId) {
        initializeSmsFeature(eventId);
    }
});
```

## API Integration Checklist

- [ ] Load registrations on page load
- [ ] Display registrations in table format
- [ ] Add checkbox selection for registrations
- [ ] Implement "Select All" functionality
- [ ] Show SMS composition modal
- [ ] Validate message length (1-1600 characters)
- [ ] Display character count
- [ ] Send POST request to `/api/v1/events/{eventId}/registrations/send-sms`
- [ ] Handle success response
- [ ] Handle error response with user-friendly message
- [ ] Show loading state during SMS sending
- [ ] Close modal on success
- [ ] Reset form after sending
- [ ] Add proper error handling and logging

## Example Usage

```javascript
// Page HTML
<button id="send-bulk-sms-btn" class="btn btn-success">Send Bulk SMS</button>

// Initialize
const eventId = 123; // Get from URL or page context
initializeSmsFeature(eventId);

// User selects registrations and sends SMS
// Frontend collects selected emails and message
// POST request sent to API
// SMS sent to all selected registrations
```

