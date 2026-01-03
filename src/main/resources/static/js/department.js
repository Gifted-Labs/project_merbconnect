/**
 * Department management module for the Academic Management System
 */

const Department = {
    // Current page for pagination
    currentPage: 0,

    // Page size for pagination
    pageSize: 10,

    // Total number of pages
    totalPages: 0,

    // Current department being edited
    currentDepartment: null,

    // Current faculty ID filter
    currentFacultyId: null,

    /**
     * Initialize department module
     */
    init: function() {
        this.bindEvents();
    },

    /**
     * Bind event listeners
     */
    bindEvents: function() {
        // Navigation link
        const departmentLink = document.querySelectorAll('[data-page="department"]');
        departmentLink.forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                this.showDepartmentSection();
            });
        });
    },

    /**
     * Show department section
     */
    showDepartmentSection: function() {
        // Hide other sections
        document.getElementById('dashboard-container').classList.add('hidden');
        document.getElementById('college-container').classList.add('hidden');
        document.getElementById('faculty-container').classList.add('hidden');
        document.getElementById('program-container').classList.add('hidden');

        // Show department section
        const departmentContainer = document.getElementById('department-container');
        departmentContainer.classList.remove('hidden');

        // Reset faculty filter
        this.currentFacultyId = null;

        // Render department section
        this.renderDepartmentSection();
    },

    /**
     * Show departments by faculty
     * @param {number} facultyId - Faculty ID
     */
    showDepartmentsByFaculty: function(facultyId) {
        // Hide other sections
        document.getElementById('dashboard-container').classList.add('hidden');
        document.getElementById('college-container').classList.add('hidden');
        document.getElementById('faculty-container').classList.add('hidden');
        document.getElementById('program-container').classList.add('hidden');

        // Show department section
        const departmentContainer = document.getElementById('department-container');
        departmentContainer.classList.remove('hidden');

        // Set faculty filter
        this.currentFacultyId = facultyId;

        // Render department section
        this.renderDepartmentSection();
    },

    /**
     * Render department section
     */
    renderDepartmentSection: function() {
        const departmentContainer = document.getElementById('department-container');
        departmentContainer.innerHTML = '';

        // Create section header
        const header = document.createElement('div');
        header.className = 'flex justify-between items-center mb-6';

        let headerTitle = 'Departments';
        if (this.currentFacultyId) {
            headerTitle += ' by Faculty';
        }

        header.innerHTML = `
            <h1 class="text-3xl font-bold text-gray-800">${headerTitle}</h1>
            <div class="flex space-x-2">
                <button id="create-department-btn" class="btn btn-primary">
                    <i class="fas fa-plus mr-2"></i> Add Department
                </button>
            </div>
        `;
        departmentContainer.appendChild(header);

        // Create filters
        if (!this.currentFacultyId) {
            const filters = document.createElement('div');
            filters.className = 'mb-6 flex flex-wrap gap-4';
            filters.innerHTML = `
                <div class="w-full md:w-auto">
                    <label for="faculty-filter" class="block text-sm font-medium text-gray-700 mb-1">Filter by Faculty</label>
                    <select id="faculty-filter" class="form-input">
                        <option value="">All Faculties</option>
                        <!-- Faculty options will be added here -->
                    </select>
                </div>
                <div class="w-full md:w-auto">
                    <label for="college-filter" class="block text-sm font-medium text-gray-700 mb-1">Filter by College</label>
                    <select id="college-filter" class="form-input">
                        <option value="">All Colleges</option>
                        <!-- College options will be added here -->
                    </select>
                </div>
            `;
            departmentContainer.appendChild(filters);

            // Load filter options
            this.loadFilterOptions();
        }

        // Create search bar
        const searchBar = document.createElement('div');
        searchBar.className = 'mb-6';
        searchBar.innerHTML = `
            <div class="flex">
                <input type="text" id="department-search" placeholder="Search departments..." class="form-input rounded-r-none">
                <button id="department-search-btn" class="btn btn-primary rounded-l-none">Search</button>
            </div>
        `;
        departmentContainer.appendChild(searchBar);

        // Create departments table
        const tableContainer = document.createElement('div');
        tableContainer.className = 'bg-white rounded-lg shadow-md overflow-hidden';
        tableContainer.innerHTML = `
            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Department Name</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Faculty</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                        </tr>
                    </thead>
                    <tbody id="departments-table-body" class="bg-white divide-y divide-gray-200">
                        <tr>
                            <td colspan="4" class="px-6 py-4 text-center text-gray-500">Loading departments...</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div id="department-pagination" class="px-6 py-4 bg-gray-50 flex justify-center">
                <!-- Pagination will be added here -->
            </div>
        `;
        departmentContainer.appendChild(tableContainer);

        // Add event listeners
        document.getElementById('create-department-btn').addEventListener('click', this.showCreateDepartmentForm.bind(this));
        document.getElementById('department-search-btn').addEventListener('click', this.handleDepartmentSearch.bind(this));
        document.getElementById('department-search').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.handleDepartmentSearch();
            }
        });

        // Add filter event listeners
        if (!this.currentFacultyId) {
            document.getElementById('faculty-filter').addEventListener('change', this.handleFacultyFilter.bind(this));
            document.getElementById('college-filter').addEventListener('change', this.handleCollegeFilter.bind(this));
        }

        // Load departments
        if (this.currentFacultyId) {
            this.loadDepartmentsByFaculty(this.currentFacultyId);
        } else {
            this.loadDepartments();
        }
    },

    /**
     * Load filter options
     */
    loadFilterOptions: async function() {
        try {
            Utils.showLoading();

            // Load faculties and colleges
            const [faculties, colleges] = await Promise.all([
                API.faculty.getAll(),
                API.college.getAll()
            ]);

            Utils.hideLoading();

            // Populate faculty filter
            const facultyFilter = document.getElementById('faculty-filter');
            faculties.forEach(faculty => {
                const option = document.createElement('option');
                option.value = faculty.id;
                option.textContent = faculty.facultyName;
                facultyFilter.appendChild(option);
            });

            // Populate college filter
            const collegeFilter = document.getElementById('college-filter');
            colleges.forEach(college => {
                const option = document.createElement('option');
                option.value = college.id;
                option.textContent = college.collegeName;
                collegeFilter.appendChild(option);
            });
        } catch (error) {
            Utils.hideLoading();
            console.error('Error loading filter options:', error);
        }
    },

    /**
     * Load departments with pagination
     */
    loadDepartments: async function() {
        try {
            Utils.showLoading();
            console.log('Loading departments, page:', this.currentPage, 'size:', this.pageSize);

            // Check authentication status
            if (!API.isAuthenticated()) {
                console.warn('User is not authenticated. Redirecting to login.');
                Utils.hideLoading();
                Utils.showError('Please log in to access departments');
                return;
            }

            const response = await API.department.getPaginated(this.currentPage, this.pageSize);
            console.log('Departments loaded successfully:', response);

            Utils.hideLoading();

            // Update pagination info
            this.totalPages = response.totalPages;

            // Render departments
            this.renderDepartments(response.content);

            // Render pagination
            this.renderPagination();
        } catch (error) {
            Utils.hideLoading();
            console.error('Error loading departments:', error);

            // Check for authentication errors
            if (error.message && (
                error.message.includes('unauthorized') ||
                error.message.includes('Unauthorized') ||
                error.message.includes('forbidden') ||
                error.message.includes('Forbidden')
            )) {
                Utils.showError('Authentication error. Please log in again.');
                // Force logout and redirect to login
                if (window.Auth) {
                    window.Auth.handleLogout();
                }
            } else {
                Utils.showError(`Failed to load departments: ${error.message || 'Unknown error'}`);
            }
        }
    },

    /**
     * Load departments by faculty
     * @param {number} facultyId - Faculty ID
     */
    loadDepartmentsByFaculty: async function(facultyId) {
        try {
            Utils.showLoading();

            const departments = await API.department.getByFacultyId(facultyId);

            Utils.hideLoading();

            // Render departments without pagination
            this.renderDepartments(departments);

            // Hide pagination
            document.getElementById('department-pagination').innerHTML = '';
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to load departments');
            console.error('Error loading departments by faculty:', error);
        }
    },

    /**
     * Render departments in table
     * @param {Array} departments - Array of department objects
     */
    renderDepartments: function(departments) {
        const tableBody = document.getElementById('departments-table-body');

        if (!departments || departments.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="4" class="px-6 py-4 text-center text-gray-500">No departments found</td>
                </tr>
            `;
            return;
        }

        tableBody.innerHTML = '';

        departments.forEach(department => {
            const row = document.createElement('tr');
            row.className = 'hover:bg-gray-50';
            row.innerHTML = `
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${department.id}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${department.departmentName}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${department.facultyName || 'N/A'}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    <button class="text-primary hover:text-primary-dark mr-2 edit-department" data-id="${department.id}">Edit</button>
                    <button class="text-red-600 hover:text-red-800 delete-department" data-id="${department.id}">Delete</button>
                    <button class="text-blue-600 hover:text-blue-800 ml-2 view-programs" data-id="${department.id}">View Programs</button>
                </td>
            `;
            tableBody.appendChild(row);

            // Add event listeners
            row.querySelector('.edit-department').addEventListener('click', () => this.handleEditDepartment(department.id));
            row.querySelector('.delete-department').addEventListener('click', () => this.handleDeleteDepartment(department.id));
            row.querySelector('.view-programs').addEventListener('click', () => this.handleViewPrograms(department.id));
        });
    },

    /**
     * Render pagination controls
     */
    renderPagination: function() {
        const paginationContainer = document.getElementById('department-pagination');
        paginationContainer.innerHTML = '';

        if (this.totalPages <= 1) {
            return;
        }

        const pagination = Utils.createPagination(this.currentPage, this.totalPages, (page) => {
            this.currentPage = page;
            this.loadDepartments();
        });

        paginationContainer.appendChild(pagination);
    },

    /**
     * Show create department form
     */
    showCreateDepartmentForm: async function() {
        try {
            Utils.showLoading();

            // Load faculties for dropdown
            const faculties = await API.faculty.getAll();

            Utils.hideLoading();

            let facultyOptions = '';
            faculties.forEach(faculty => {
                facultyOptions += `<option value="${faculty.id}">${faculty.facultyName}</option>`;
            });

            Swal.fire({
                title: 'Create New Department',
                html: `
                    <div class="mb-4">
                        <label for="department-name" class="block text-sm font-medium text-gray-700 mb-1">Department Name</label>
                        <input id="department-name" class="form-input w-full" placeholder="Enter department name">
                    </div>
                    <div class="mb-4">
                        <label for="faculty-id" class="block text-sm font-medium text-gray-700 mb-1">Faculty</label>
                        <select id="faculty-id" class="form-input w-full">
                            <option value="">Select Faculty</option>
                            ${facultyOptions}
                        </select>
                    </div>
                `,
                showCancelButton: true,
                confirmButtonText: 'Create',
                confirmButtonColor: '#3B82F6',
                cancelButtonColor: '#6B7280',
                preConfirm: () => {
                    const departmentName = document.getElementById('department-name').value;
                    const facultyId = document.getElementById('faculty-id').value;

                    if (!departmentName) {
                        Swal.showValidationMessage('Department name is required');
                        return false;
                    }

                    if (!facultyId) {
                        Swal.showValidationMessage('Faculty is required');
                        return false;
                    }

                    return {
                        departmentName,
                        facultyId: parseInt(facultyId)
                    };
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    this.createDepartment(result.value);
                }
            });

            // Set default faculty if filtering by faculty
            if (this.currentFacultyId) {
                document.getElementById('faculty-id').value = this.currentFacultyId;
            }
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to load faculties');
        }
    },

    /**
     * Create new department
     * @param {Object} departmentData - Department data
     */
    createDepartment: async function(departmentData) {
        try {
            Utils.showLoading();
            await API.department.create(departmentData);
            Utils.hideLoading();
            Utils.showSuccess('Department created successfully');

            if (this.currentFacultyId) {
                this.loadDepartmentsByFaculty(this.currentFacultyId);
            } else {
                this.loadDepartments();
            }
        } catch (error) {
            Utils.hideLoading();
            Utils.showError(error.message || 'Failed to create department');
        }
    },

    /**
     * Handle edit department
     * @param {number} departmentId - Department ID
     */
    handleEditDepartment: async function(departmentId) {
        try {
            Utils.showLoading();

            // Load department and faculties
            const [department, faculties] = await Promise.all([
                API.department.getById(departmentId),
                API.faculty.getAll()
            ]);

            Utils.hideLoading();

            this.currentDepartment = department;

            let facultyOptions = '';
            faculties.forEach(faculty => {
                const selected = department.faculty && department.faculty.id === faculty.id ? 'selected' : '';
                facultyOptions += `<option value="${faculty.id}" ${selected}>${faculty.facultyName}</option>`;
            });

            Swal.fire({
                title: 'Edit Department',
                html: `
                    <div class="mb-4">
                        <label for="edit-department-name" class="block text-sm font-medium text-gray-700 mb-1">Department Name</label>
                        <input id="edit-department-name" class="form-input w-full" value="${department.departmentName}">
                    </div>
                    <div class="mb-4">
                        <label for="edit-faculty-id" class="block text-sm font-medium text-gray-700 mb-1">Faculty</label>
                        <select id="edit-faculty-id" class="form-input w-full">
                            <option value="">Select Faculty</option>
                            ${facultyOptions}
                        </select>
                    </div>
                `,
                showCancelButton: true,
                confirmButtonText: 'Update',
                confirmButtonColor: '#3B82F6',
                cancelButtonColor: '#6B7280',
                preConfirm: () => {
                    const departmentName = document.getElementById('edit-department-name').value;
                    const facultyId = document.getElementById('edit-faculty-id').value;

                    if (!departmentName) {
                        Swal.showValidationMessage('Department name is required');
                        return false;
                    }

                    if (!facultyId) {
                        Swal.showValidationMessage('Faculty is required');
                        return false;
                    }

                    return {
                        departmentName,
                        facultyId: parseInt(facultyId)
                    };
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    this.updateDepartment(departmentId, result.value);
                }
            });
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to load department details');
        }
    },

    /**
     * Update department
     * @param {number} departmentId - Department ID
     * @param {Object} departmentData - Updated department data
     */
    updateDepartment: async function(departmentId, departmentData) {
        try {
            Utils.showLoading();
            await API.department.update(departmentId, departmentData);
            Utils.hideLoading();
            Utils.showSuccess('Department updated successfully');

            if (this.currentFacultyId) {
                this.loadDepartmentsByFaculty(this.currentFacultyId);
            } else {
                this.loadDepartments();
            }
        } catch (error) {
            Utils.hideLoading();
            Utils.showError(error.message || 'Failed to update department');
        }
    },

    /**
     * Handle delete department
     * @param {number} departmentId - Department ID
     */
    handleDeleteDepartment: function(departmentId) {
        Utils.showConfirm(
            'Delete Department',
            'Are you sure you want to delete this department? This action cannot be undone.',
            'Delete',
            async () => {
                try {
                    Utils.showLoading();
                    await API.department.delete(departmentId);
                    Utils.hideLoading();
                    Utils.showSuccess('Department deleted successfully');

                    if (this.currentFacultyId) {
                        this.loadDepartmentsByFaculty(this.currentFacultyId);
                    } else {
                        this.loadDepartments();
                    }
                } catch (error) {
                    Utils.hideLoading();
                    Utils.showError(error.message || 'Failed to delete department');
                }
            }
        );
    },

    /**
     * Handle view programs
     * @param {number} departmentId - Department ID
     */
    handleViewPrograms: function(departmentId) {
        // This will be implemented in the Program module
        if (window.Program) {
            window.Program.showProgramsByDepartment(departmentId);
        } else {
            Utils.showError('Program module not loaded');
        }
    },

    /**
     * Handle department search
     */
    handleDepartmentSearch: async function() {
        const searchTerm = document.getElementById('department-search').value.trim();

        if (!searchTerm) {
            if (this.currentFacultyId) {
                this.loadDepartmentsByFaculty(this.currentFacultyId);
            } else {
                this.currentPage = 0;
                this.loadDepartments();
            }
            return;
        }

        try {
            Utils.showLoading();
            const departments = await API.department.search(searchTerm);
            Utils.hideLoading();

            // Render departments without pagination
            this.renderDepartments(departments);

            // Hide pagination
            document.getElementById('department-pagination').innerHTML = '';
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to search departments');
        }
    },

    /**
     * Handle faculty filter change
     */
    handleFacultyFilter: function() {
        const facultyId = document.getElementById('faculty-filter').value;

        if (facultyId) {
            this.loadDepartmentsByFaculty(facultyId);
        } else {
            this.currentPage = 0;
            this.loadDepartments();
        }
    },

    /**
     * Handle college filter change
     */
    handleCollegeFilter: async function() {
        const collegeId = document.getElementById('college-filter').value;

        if (!collegeId) {
            this.currentPage = 0;
            this.loadDepartments();
            return;
        }

        try {
            Utils.showLoading();
            const departments = await API.department.getByCollegeId(collegeId);
            Utils.hideLoading();

            // Render departments without pagination
            this.renderDepartments(departments);

            // Hide pagination
            document.getElementById('department-pagination').innerHTML = '';
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to filter departments by college');
        }
    }
};

// Initialize department module when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    Department.init();
});

// Export Department object
window.Department = Department;
