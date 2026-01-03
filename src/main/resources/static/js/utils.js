/**
 * Utility functions for the Academic Management System
 */

const Utils = {
    /**
     * Show loading overlay
     */
    showLoading: function() {
        document.getElementById('loading-overlay').classList.remove('hidden');
    },

    /**
     * Hide loading overlay
     */
    hideLoading: function() {
        document.getElementById('loading-overlay').classList.add('hidden');
    },

    /**
     * Show success message using SweetAlert
     * @param {string} message - Success message to display
     * @param {Function} callback - Optional callback function to execute after confirmation
     */
    showSuccess: function(message, callback) {
        Swal.fire({
            icon: 'success',
            title: 'Success',
            text: message,
            confirmButtonColor: '#3B82F6'
        }).then(() => {
            if (callback && typeof callback === 'function') {
                callback();
            }
        });
    },

    /**
     * Show error message using SweetAlert
     * @param {string} message - Error message to display
     */
    showError: function(message) {
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: message,
            confirmButtonColor: '#3B82F6'
        });
    },

    /**
     * Show confirmation dialog using SweetAlert
     * @param {string} title - Dialog title
     * @param {string} text - Dialog text
     * @param {string} confirmButtonText - Text for confirm button
     * @param {Function} onConfirm - Function to execute on confirmation
     */
    showConfirm: function(title, text, confirmButtonText, onConfirm) {
        Swal.fire({
            title: title,
            text: text,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#EF4444',
            cancelButtonColor: '#6B7280',
            confirmButtonText: confirmButtonText
        }).then((result) => {
            if (result.isConfirmed && onConfirm && typeof onConfirm === 'function') {
                onConfirm();
            }
        });
    },

    /**
     * Format date string to readable format
     * @param {string} dateString - ISO date string
     * @returns {string} Formatted date string
     */
    formatDate: function(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    },

    /**
     * Validate email format
     * @param {string} email - Email to validate
     * @returns {boolean} True if email is valid
     */
    validateEmail: function(email) {
        const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(String(email).toLowerCase());
    },

    /**
     * Create pagination controls
     * @param {number} currentPage - Current page number
     * @param {number} totalPages - Total number of pages
     * @param {Function} onPageChange - Function to call when page changes
     * @returns {HTMLElement} Pagination container element
     */
    createPagination: function(currentPage, totalPages, onPageChange) {
        const paginationContainer = document.createElement('div');
        paginationContainer.className = 'pagination';

        // Previous button
        const prevButton = document.createElement('button');
        prevButton.className = `pagination-item ${currentPage === 0 ? 'opacity-50 cursor-not-allowed' : ''}`;
        prevButton.textContent = 'Previous';
        prevButton.disabled = currentPage === 0;
        prevButton.addEventListener('click', () => {
            if (currentPage > 0) {
                onPageChange(currentPage - 1);
            }
        });
        paginationContainer.appendChild(prevButton);

        // Page numbers
        const maxVisiblePages = 5;
        let startPage = Math.max(0, currentPage - Math.floor(maxVisiblePages / 2));
        let endPage = Math.min(totalPages - 1, startPage + maxVisiblePages - 1);

        if (endPage - startPage + 1 < maxVisiblePages) {
            startPage = Math.max(0, endPage - maxVisiblePages + 1);
        }

        for (let i = startPage; i <= endPage; i++) {
            const pageButton = document.createElement('button');
            pageButton.className = `pagination-item ${i === currentPage ? 'active' : ''}`;
            pageButton.textContent = i + 1;
            pageButton.addEventListener('click', () => {
                onPageChange(i);
            });
            paginationContainer.appendChild(pageButton);
        }

        // Next button
        const nextButton = document.createElement('button');
        nextButton.className = `pagination-item ${currentPage === totalPages - 1 ? 'opacity-50 cursor-not-allowed' : ''}`;
        nextButton.textContent = 'Next';
        nextButton.disabled = currentPage === totalPages - 1;
        nextButton.addEventListener('click', () => {
            if (currentPage < totalPages - 1) {
                onPageChange(currentPage + 1);
            }
        });
        paginationContainer.appendChild(nextButton);

        return paginationContainer;
    },

    /**
     * Create a form input field
     * @param {string} type - Input type (text, email, password, etc.)
     * @param {string} id - Input ID
     * @param {string} label - Input label
     * @param {string} value - Input value
     * @param {boolean} required - Whether the input is required
     * @param {string} placeholder - Input placeholder
     * @returns {HTMLElement} Form group element containing label and input
     */
    createFormInput: function(type, id, label, value = '', required = false, placeholder = '') {
        const formGroup = document.createElement('div');
        formGroup.className = 'mb-4';

        const labelElement = document.createElement('label');
        labelElement.htmlFor = id;
        labelElement.className = 'form-label';
        labelElement.textContent = label;
        if (required) {
            const requiredSpan = document.createElement('span');
            requiredSpan.className = 'text-red-500 ml-1';
            requiredSpan.textContent = '*';
            labelElement.appendChild(requiredSpan);
        }
        formGroup.appendChild(labelElement);

        const input = document.createElement('input');
        input.type = type;
        input.id = id;
        input.name = id;
        input.className = 'form-input';
        input.value = value;
        input.placeholder = placeholder;
        input.required = required;
        formGroup.appendChild(input);

        return formGroup;
    },

    /**
     * Create a select dropdown
     * @param {string} id - Select ID
     * @param {string} label - Select label
     * @param {Array} options - Array of options {value, text}
     * @param {string} selectedValue - Selected value
     * @param {boolean} required - Whether the select is required
     * @returns {HTMLElement} Form group element containing label and select
     */
    createFormSelect: function(id, label, options, selectedValue = '', required = false) {
        const formGroup = document.createElement('div');
        formGroup.className = 'mb-4';

        const labelElement = document.createElement('label');
        labelElement.htmlFor = id;
        labelElement.className = 'form-label';
        labelElement.textContent = label;
        if (required) {
            const requiredSpan = document.createElement('span');
            requiredSpan.className = 'text-red-500 ml-1';
            requiredSpan.textContent = '*';
            labelElement.appendChild(requiredSpan);
        }
        formGroup.appendChild(labelElement);

        const select = document.createElement('select');
        select.id = id;
        select.name = id;
        select.className = 'form-input';
        select.required = required;

        // Add empty option
        const emptyOption = document.createElement('option');
        emptyOption.value = '';
        emptyOption.textContent = '-- Select --';
        select.appendChild(emptyOption);

        // Add options
        options.forEach(option => {
            const optionElement = document.createElement('option');
            optionElement.value = option.value;
            optionElement.textContent = option.text;
            if (option.value == selectedValue) {
                optionElement.selected = true;
            }
            select.appendChild(optionElement);
        });

        formGroup.appendChild(select);

        return formGroup;
    }
};

// Export Utils object
window.Utils = Utils;
