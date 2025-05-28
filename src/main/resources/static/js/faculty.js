/**
 * Faculty management module for the Academic Management System
 */

const Faculty = {
    // Current page for pagination
    currentPage: 0,

    // Page size for pagination
    pageSize: 10,

    // Total number of pages
    totalPages: 0,

    // Current faculty being edited
    currentFaculty: null,

    // Current college ID filter
    currentCollegeId: null,

    /**
     * Initialize faculty module
     */
    init: function() {
        this.bindEvents();
    },

    /**
     * Bind event listeners
     */
    bindEvents: function() {
        // Navigation link
        const facultyLink = document.querySelectorAll('[data-page="faculty"]');
        facultyLink.forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                this.showFacultySection();
            });
        });
    },

    /**
     * Show faculty section
     */
    showFacultySection: function() {
        // Hide other sections
        document.getElementById('dashboard-container').classList.add('hidden');
        document.getElementById('college-container').classList.add('hidden');
        document.getElementById('department-container').classList.add('hidden');
        document.getElementById('program-container').classList.add('hidden');

        // Show faculty section
        const facultyContainer = document.getElementById('faculty-container');
        facultyContainer.classList.remove('hidden');

        // Reset college filter
        this.currentCollegeId = null;

        // Render faculty section
        this.renderFacultySection();
    },

    /**
     * Show faculties by college
     * @param {number} collegeId - College ID
     */
    showFacultiesByCollege: function(collegeId) {
        // Hide other sections
        document.getElementById('dashboard-container').classList.add('hidden');
        document.getElementById('college-container').classList.add('hidden');
        document.getElementById('department-container').classList.add('hidden');
        document.getElementById('program-container').classList.add('hidden');

        // Show faculty section
        const facultyContainer = document.getElementById('faculty-container');
        facultyContainer.classList.remove('hidden');

        // Set college filter
        this.currentCollegeId = collegeId;

        // Render faculty section
        this.renderFacultySection();
    },

    /**
     * Render faculty section
     */
    renderFacultySection: function() {
        const facultyContainer = document.getElementById('faculty-container');
        facultyContainer.innerHTML = '';

        // Create section header
        const header = document.createElement('div');
        header.className = 'flex justify-between items-center mb-6';

        let headerTitle = 'Faculties';
        if (this.currentCollegeId) {
            headerTitle += ' by College';
        }

        header.innerHTML = `
            <h1 class="text-3xl font-bold text-gray-800">${headerTitle}</h1>
            <div class="flex space-x-2">
                <button id="create-faculty-btn" class="btn btn-primary">
                    <i class="fas fa-plus mr-2"></i> Add Faculty
                </button>
            </div>
        `;
        facultyContainer.appendChild(header);

        // Create filters
        if (!this.currentCollegeId) {
            const filters = document.createElement('div');
            filters.className = 'mb-6';
            filters.innerHTML = `
                <div>
                    <label for="college-filter" class="block text-sm font-medium text-gray-700 mb-1">Filter by College</label>
                    <select id="college-filter" class="form-input">
                        <option value="">All Colleges</option>
                        <!-- College options will be added here -->
                    </select>
                </div>
            `;
            facultyContainer.appendChild(filters);

            // Load filter options
            this.loadFilterOptions();
        }

        // Create search bar
        const searchBar = document.createElement('div');
        searchBar.className = 'mb-6';
        searchBar.innerHTML = `
            <div class="flex">
                <input type="text" id="faculty-search" placeholder="Search faculties..." class="form-input rounded-r-none">
                <button id="faculty-search-btn" class="btn btn-primary rounded-l-none">Search</button>
            </div>
        `;
        facultyContainer.appendChild(searchBar);

        // Create faculties table
        const tableContainer = document.createElement('div');
        tableContainer.className = 'bg-white rounded-lg shadow-md overflow-hidden';
        tableContainer.innerHTML = `
            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Faculty Name</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">College</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                        </tr>
                    </thead>
                    <tbody id="faculties-table-body" class="bg-white divide-y divide-gray-200">
                        <tr>
                            <td colspan="4" class="px-6 py-4 text-center text-gray-500">Loading faculties...</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div id="faculty-pagination" class="px-6 py-4 bg-gray-50 flex justify-center">
                <!-- Pagination will be added here -->
            </div>
        `;
        facultyContainer.appendChild(tableContainer);

        // Add event listeners
        document.getElementById('create-faculty-btn').addEventListener('click', this.showCreateFacultyForm.bind(this));
        document.getElementById('faculty-search-btn').addEventListener('click', this.handleFacultySearch.bind(this));
        document.getElementById('faculty-search').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.handleFacultySearch();
            }
        });

        // Add filter event listeners
        if (!this.currentCollegeId) {
            document.getElementById('college-filter').addEventListener('change', this.handleCollegeFilter.bind(this));
        }

        // Load faculties
        if (this.currentCollegeId) {
            this.loadFacultiesByCollege(this.currentCollegeId);
        } else {
            this.loadFaculties();
        }
    },

    /**
     * Load filter options
     */
    loadFilterOptions: async function() {
        try {
            Utils.showLoading();

            // Load colleges
            const colleges = await API.college.getAll();

            Utils.hideLoading();

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
     * Load faculties with pagination
     */
    loadFaculties: async function() {
        try {
            Utils.showLoading();

            const response = await API.faculty.getPaginated(this.currentPage, this.pageSize);

            Utils.hideLoading();

            // Update pagination info
            this.totalPages = response.totalPages;

            // Render faculties
            this.renderFaculties(response.content);

            // Render pagination
            this.renderPagination();
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to load faculties');
            console.error('Error loading faculties:', error);
        }
    },

    /**
     * Load faculties by college
     * @param {number} collegeId - College ID
     */
    loadFacultiesByCollege: async function(collegeId) {
        try {
            Utils.showLoading();

            const faculties = await API.faculty.getByCollegeId(collegeId);

            Utils.hideLoading();

            // Render faculties without pagination
            this.renderFaculties(faculties);

            // Hide pagination
            document.getElementById('faculty-pagination').innerHTML = '';
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to load faculties');
            console.error('Error loading faculties by college:', error);
        }
    },

    /**
     * Render faculties in table
     * @param {Array} faculties - Array of faculty objects
     */
    renderFaculties: function(faculties) {
        const tableBody = document.getElementById('faculties-table-body');

        if (!faculties || faculties.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="4" class="px-6 py-4 text-center text-gray-500">No faculties found</td>
                </tr>
            `;
            return;
        }

        tableBody.innerHTML = '';

        faculties.forEach(faculty => {
            const row = document.createElement('tr');
            row.className = 'hover:bg-gray-50';
            row.innerHTML = `
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${faculty.id}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${faculty.facultyName}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${faculty.collegeName || 'N/A'}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    <button class="text-primary hover:text-primary-dark mr-2 edit-faculty" data-id="${faculty.id}">Edit</button>
                    <button class="text-red-600 hover:text-red-800 delete-faculty" data-id="${faculty.id}">Delete</button>
                    <button class="text-blue-600 hover:text-blue-800 ml-2 view-departments" data-id="${faculty.id}">View Departments</button>
                </td>
            `;
            tableBody.appendChild(row);

            // Add event listeners
            row.querySelector('.edit-faculty').addEventListener('click', () => this.handleEditFaculty(faculty.id));
            row.querySelector('.delete-faculty').addEventListener('click', () => this.handleDeleteFaculty(faculty.id));
            row.querySelector('.view-departments').addEventListener('click', () => this.handleViewDepartments(faculty.id));
        });
    },

    /**
     * Render pagination controls
     */
    renderPagination: function() {
        const paginationContainer = document.getElementById('faculty-pagination');
        paginationContainer.innerHTML = '';

        if (this.totalPages <= 1) {
            return;
        }

        const pagination = Utils.createPagination(this.currentPage, this.totalPages, (page) => {
            this.currentPage = page;
            this.loadFaculties();
        });

        paginationContainer.appendChild(pagination);
    },

    /**
     * Show create faculty form
     */
    showCreateFacultyForm: async function() {
        try {
            Utils.showLoading();

            // Load colleges for dropdown
            const colleges = await API.college.getAll();

            Utils.hideLoading();

            let collegeOptions = '';
            colleges.forEach(college => {
                collegeOptions += `<option value="${college.id}">${college.collegeName}</option>`;
            });

            Swal.fire({
                title: 'Create New Faculty',
                html: `
                    <div class="mb-4">
                        <label for="faculty-name" class="block text-sm font-medium text-gray-700 mb-1">Faculty Name</label>
                        <input id="faculty-name" class="form-input w-full" placeholder="Enter faculty name">
                    </div>
                    <div class="mb-4">
                        <label for="college-id" class="block text-sm font-medium text-gray-700 mb-1">College</label>
                        <select id="college-id" class="form-input w-full">
                            <option value="">Select College</option>
                            ${collegeOptions}
                        </select>
                    </div>
                `,
                showCancelButton: true,
                confirmButtonText: 'Create',
                confirmButtonColor: '#3B82F6',
                cancelButtonColor: '#6B7280',
                preConfirm: () => {
                    const facultyName = document.getElementById('faculty-name').value;
                    const collegeId = document.getElementById('college-id').value;

                    if (!facultyName) {
                        Swal.showValidationMessage('Faculty name is required');
                        return false;
                    }

                    if (!collegeId) {
                        Swal.showValidationMessage('College is required');
                        return false;
                    }

                    return {
                        facultyName,
                        collegeId: parseInt(collegeId)
                    };
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    this.createFaculty(result.value);
                }
            });

            // Set default college if filtering by college
            if (this.currentCollegeId) {
                document.getElementById('college-id').value = this.currentCollegeId;
            }
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to load colleges');
        }
    },

    /**
     * Create new faculty
     * @param {Object} facultyData - Faculty data
     */
    createFaculty: async function(facultyData) {
        try {
            Utils.showLoading();
            await API.faculty.create(facultyData);
            Utils.hideLoading();
            Utils.showSuccess('Faculty created successfully');

            if (this.currentCollegeId) {
                this.loadFacultiesByCollege(this.currentCollegeId);
            } else {
                this.loadFaculties();
            }
        } catch (error) {
            Utils.hideLoading();
            Utils.showError(error.message || 'Failed to create faculty');
        }
    },

    /**
     * Handle edit faculty
     * @param {number} facultyId - Faculty ID
     */
    handleEditFaculty: async function(facultyId) {
        try {
            Utils.showLoading();

            // Load faculty and colleges
            const [faculty, colleges] = await Promise.all([
                API.faculty.getById(facultyId),
                API.college.getAll()
            ]);

            Utils.hideLoading();

            this.currentFaculty = faculty;

            let collegeOptions = '';
            colleges.forEach(college => {
                const selected = faculty.college && faculty.college.id === college.id ? 'selected' : '';
                collegeOptions += `<option value="${college.id}" ${selected}>${college.collegeName}</option>`;
            });

            Swal.fire({
                title: 'Edit Faculty',
                html: `
                    <div class="mb-4">
                        <label for="edit-faculty-name" class="block text-sm font-medium text-gray-700 mb-1">Faculty Name</label>
                        <input id="edit-faculty-name" class="form-input w-full" value="${faculty.facultyName}">
                    </div>
                    <div class="mb-4">
                        <label for="edit-college-id" class="block text-sm font-medium text-gray-700 mb-1">College</label>
                        <select id="edit-college-id" class="form-input w-full">
                            <option value="">Select College</option>
                            ${collegeOptions}
                        </select>
                    </div>
                `,
                showCancelButton: true,
                confirmButtonText: 'Update',
                confirmButtonColor: '#3B82F6',
                cancelButtonColor: '#6B7280',
                preConfirm: () => {
                    const facultyName = document.getElementById('edit-faculty-name').value;
                    const collegeId = document.getElementById('edit-college-id').value;

                    if (!facultyName) {
                        Swal.showValidationMessage('Faculty name is required');
                        return false;
                    }

                    if (!collegeId) {
                        Swal.showValidationMessage('College is required');
                        return false;
                    }

                    return {
                        facultyName,
                        collegeId: parseInt(collegeId)
                    };
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    this.updateFaculty(facultyId, result.value);
                }
            });
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to load faculty details');
        }
    },

    /**
     * Update faculty
     * @param {number} facultyId - Faculty ID
     * @param {Object} facultyData - Updated faculty data
     */
    updateFaculty: async function(facultyId, facultyData) {
        try {
            Utils.showLoading();
            await API.faculty.update(facultyId, facultyData);
            Utils.hideLoading();
            Utils.showSuccess('Faculty updated successfully');

            if (this.currentCollegeId) {
                this.loadFacultiesByCollege(this.currentCollegeId);
            } else {
                this.loadFaculties();
            }
        } catch (error) {
            Utils.hideLoading();
            Utils.showError(error.message || 'Failed to update faculty');
        }
    },

    /**
     * Handle delete faculty
     * @param {number} facultyId - Faculty ID
     */
    handleDeleteFaculty: function(facultyId) {
        Utils.showConfirm(
            'Delete Faculty',
            'Are you sure you want to delete this faculty? This action cannot be undone.',
            'Delete',
            async () => {
                try {
                    Utils.showLoading();
                    await API.faculty.delete(facultyId);
                    Utils.hideLoading();
                    Utils.showSuccess('Faculty deleted successfully');

                    if (this.currentCollegeId) {
                        this.loadFacultiesByCollege(this.currentCollegeId);
                    } else {
                        this.loadFaculties();
                    }
                } catch (error) {
                    Utils.hideLoading();
                    Utils.showError(error.message || 'Failed to delete faculty');
                }
            }
        );
    },

    /**
     * Handle view departments
     * @param {number} facultyId - Faculty ID
     */
    handleViewDepartments: function(facultyId) {
        // This will be implemented in the Department module
        if (window.Department) {
            window.Department.showDepartmentsByFaculty(facultyId);
        } else {
            Utils.showError('Department module not loaded');
        }
    },

    /**
     * Handle faculty search
     */
    handleFacultySearch: async function() {
        const searchTerm = document.getElementById('faculty-search').value.trim();

        if (!searchTerm) {
            if (this.currentCollegeId) {
                this.loadFacultiesByCollege(this.currentCollegeId);
            } else {
                this.currentPage = 0;
                this.loadFaculties();
            }
            return;
        }

        try {
            Utils.showLoading();
            const faculties = await API.faculty.search(searchTerm);
            Utils.hideLoading();

            // Render faculties without pagination
            this.renderFaculties(faculties);

            // Hide pagination
            document.getElementById('faculty-pagination').innerHTML = '';
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to search faculties');
        }
    },

    /**
     * Handle college filter change
     */
    handleCollegeFilter: function() {
        const collegeId = document.getElementById('college-filter').value;

        if (collegeId) {
            this.loadFacultiesByCollege(collegeId);
        } else {
            this.currentPage = 0;
            this.loadFaculties();
        }
    }
};

// Initialize faculty module when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    Faculty.init();
});

// Export Faculty object
window.Faculty = Faculty;
