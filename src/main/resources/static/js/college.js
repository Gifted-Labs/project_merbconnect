/**
 * College management module for the Academic Management System
 */

const College = {
    // Current page for pagination
    currentPage: 0,
    
    // Page size for pagination
    pageSize: 10,
    
    // Total number of pages
    totalPages: 0,
    
    // Current college being edited
    currentCollege: null,

    /**
     * Initialize college module
     */
    init: function() {
        this.bindEvents();
    },

    /**
     * Bind event listeners
     */
    bindEvents: function() {
        // Navigation link
        const collegeLink = document.querySelectorAll('[data-page="college"]');
        collegeLink.forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                this.showCollegeSection();
            });
        });
    },

    /**
     * Show college section
     */
    showCollegeSection: function() {
        // Hide other sections
        document.getElementById('dashboard-container').classList.add('hidden');
        document.getElementById('faculty-container').classList.add('hidden');
        document.getElementById('department-container').classList.add('hidden');
        document.getElementById('program-container').classList.add('hidden');
        
        // Show college section
        const collegeContainer = document.getElementById('college-container');
        collegeContainer.classList.remove('hidden');
        
        // Render college section
        this.renderCollegeSection();
    },

    /**
     * Render college section
     */
    renderCollegeSection: function() {
        const collegeContainer = document.getElementById('college-container');
        collegeContainer.innerHTML = '';
        
        // Create section header
        const header = document.createElement('div');
        header.className = 'flex justify-between items-center mb-6';
        header.innerHTML = `
            <h1 class="text-3xl font-bold text-gray-800">Colleges</h1>
            <div class="flex space-x-2">
                <button id="create-college-btn" class="btn btn-primary">
                    <i class="fas fa-plus mr-2"></i> Add College
                </button>
            </div>
        `;
        collegeContainer.appendChild(header);
        
        // Create search bar
        const searchBar = document.createElement('div');
        searchBar.className = 'mb-6';
        searchBar.innerHTML = `
            <div class="flex">
                <input type="text" id="college-search" placeholder="Search colleges..." class="form-input rounded-r-none">
                <button id="college-search-btn" class="btn btn-primary rounded-l-none">Search</button>
            </div>
        `;
        collegeContainer.appendChild(searchBar);
        
        // Create colleges table
        const tableContainer = document.createElement('div');
        tableContainer.className = 'bg-white rounded-lg shadow-md overflow-hidden';
        tableContainer.innerHTML = `
            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">College Name</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                        </tr>
                    </thead>
                    <tbody id="colleges-table-body" class="bg-white divide-y divide-gray-200">
                        <tr>
                            <td colspan="3" class="px-6 py-4 text-center text-gray-500">Loading colleges...</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div id="college-pagination" class="px-6 py-4 bg-gray-50 flex justify-center">
                <!-- Pagination will be added here -->
            </div>
        `;
        collegeContainer.appendChild(tableContainer);
        
        // Add event listeners
        document.getElementById('create-college-btn').addEventListener('click', this.showCreateCollegeForm.bind(this));
        document.getElementById('college-search-btn').addEventListener('click', this.handleCollegeSearch.bind(this));
        document.getElementById('college-search').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.handleCollegeSearch();
            }
        });
        
        // Load colleges
        this.loadColleges();
    },

    /**
     * Load colleges with pagination
     */
    loadColleges: async function() {
        try {
            Utils.showLoading();
            
            const response = await API.college.getPaginated(this.currentPage, this.pageSize);
            
            Utils.hideLoading();
            
            // Update pagination info
            this.totalPages = response.totalPages;
            
            // Render colleges
            this.renderColleges(response.content);
            
            // Render pagination
            this.renderPagination();
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to load colleges');
            console.error('Error loading colleges:', error);
        }
    },

    /**
     * Render colleges in table
     * @param {Array} colleges - Array of college objects
     */
    renderColleges: function(colleges) {
        const tableBody = document.getElementById('colleges-table-body');
        
        if (!colleges || colleges.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="3" class="px-6 py-4 text-center text-gray-500">No colleges found</td>
                </tr>
            `;
            return;
        }
        
        tableBody.innerHTML = '';
        
        colleges.forEach(college => {
            const row = document.createElement('tr');
            row.className = 'hover:bg-gray-50';
            row.innerHTML = `
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${college.id}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${college.collegeName}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    <button class="text-primary hover:text-primary-dark mr-2 edit-college" data-id="${college.id}">Edit</button>
                    <button class="text-red-600 hover:text-red-800 delete-college" data-id="${college.id}">Delete</button>
                    <button class="text-blue-600 hover:text-blue-800 ml-2 view-faculties" data-id="${college.id}">View Faculties</button>
                </td>
            `;
            tableBody.appendChild(row);
            
            // Add event listeners
            row.querySelector('.edit-college').addEventListener('click', () => this.handleEditCollege(college.id));
            row.querySelector('.delete-college').addEventListener('click', () => this.handleDeleteCollege(college.id));
            row.querySelector('.view-faculties').addEventListener('click', () => this.handleViewFaculties(college.id));
        });
    },

    /**
     * Render pagination controls
     */
    renderPagination: function() {
        const paginationContainer = document.getElementById('college-pagination');
        paginationContainer.innerHTML = '';
        
        if (this.totalPages <= 1) {
            return;
        }
        
        const pagination = Utils.createPagination(this.currentPage, this.totalPages, (page) => {
            this.currentPage = page;
            this.loadColleges();
        });
        
        paginationContainer.appendChild(pagination);
    },

    /**
     * Show create college form
     */
    showCreateCollegeForm: function() {
        Swal.fire({
            title: 'Create New College',
            html: `
                <div class="mb-4">
                    <label for="college-name" class="block text-sm font-medium text-gray-700 mb-1">College Name</label>
                    <input id="college-name" class="form-input w-full" placeholder="Enter college name">
                </div>
            `,
            showCancelButton: true,
            confirmButtonText: 'Create',
            confirmButtonColor: '#3B82F6',
            cancelButtonColor: '#6B7280',
            preConfirm: () => {
                const collegeName = document.getElementById('college-name').value;
                if (!collegeName) {
                    Swal.showValidationMessage('College name is required');
                    return false;
                }
                return { collegeName };
            }
        }).then((result) => {
            if (result.isConfirmed) {
                this.createCollege(result.value);
            }
        });
    },

    /**
     * Create new college
     * @param {Object} collegeData - College data
     */
    createCollege: async function(collegeData) {
        try {
            Utils.showLoading();
            await API.college.create(collegeData);
            Utils.hideLoading();
            Utils.showSuccess('College created successfully');
            this.loadColleges();
        } catch (error) {
            Utils.hideLoading();
            Utils.showError(error.message || 'Failed to create college');
        }
    },

    /**
     * Handle edit college
     * @param {number} collegeId - College ID
     */
    handleEditCollege: async function(collegeId) {
        try {
            Utils.showLoading();
            const college = await API.college.getById(collegeId);
            Utils.hideLoading();
            
            this.currentCollege = college;
            
            Swal.fire({
                title: 'Edit College',
                html: `
                    <div class="mb-4">
                        <label for="edit-college-name" class="block text-sm font-medium text-gray-700 mb-1">College Name</label>
                        <input id="edit-college-name" class="form-input w-full" value="${college.collegeName}">
                    </div>
                `,
                showCancelButton: true,
                confirmButtonText: 'Update',
                confirmButtonColor: '#3B82F6',
                cancelButtonColor: '#6B7280',
                preConfirm: () => {
                    const collegeName = document.getElementById('edit-college-name').value;
                    if (!collegeName) {
                        Swal.showValidationMessage('College name is required');
                        return false;
                    }
                    return { collegeName };
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    this.updateCollege(collegeId, result.value);
                }
            });
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to load college details');
        }
    },

    /**
     * Update college
     * @param {number} collegeId - College ID
     * @param {Object} collegeData - Updated college data
     */
    updateCollege: async function(collegeId, collegeData) {
        try {
            Utils.showLoading();
            await API.college.update(collegeId, collegeData);
            Utils.hideLoading();
            Utils.showSuccess('College updated successfully');
            this.loadColleges();
        } catch (error) {
            Utils.hideLoading();
            Utils.showError(error.message || 'Failed to update college');
        }
    },

    /**
     * Handle delete college
     * @param {number} collegeId - College ID
     */
    handleDeleteCollege: function(collegeId) {
        Utils.showConfirm(
            'Delete College',
            'Are you sure you want to delete this college? This action cannot be undone.',
            'Delete',
            async () => {
                try {
                    Utils.showLoading();
                    await API.college.delete(collegeId);
                    Utils.hideLoading();
                    Utils.showSuccess('College deleted successfully');
                    this.loadColleges();
                } catch (error) {
                    Utils.hideLoading();
                    Utils.showError(error.message || 'Failed to delete college');
                }
            }
        );
    },

    /**
     * Handle view faculties
     * @param {number} collegeId - College ID
     */
    handleViewFaculties: function(collegeId) {
        // This will be implemented in the Faculty module
        if (window.Faculty) {
            window.Faculty.showFacultiesByCollege(collegeId);
        } else {
            Utils.showError('Faculty module not loaded');
        }
    },

    /**
     * Handle college search
     */
    handleCollegeSearch: async function() {
        const searchTerm = document.getElementById('college-search').value.trim();
        
        if (!searchTerm) {
            this.currentPage = 0;
            this.loadColleges();
            return;
        }
        
        try {
            Utils.showLoading();
            const colleges = await API.college.search(searchTerm);
            Utils.hideLoading();
            
            // Render colleges without pagination
            this.renderColleges(colleges);
            
            // Hide pagination
            document.getElementById('college-pagination').innerHTML = '';
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to search colleges');
        }
    }
};

// Initialize college module when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    College.init();
});

// Export College object
window.College = College;
