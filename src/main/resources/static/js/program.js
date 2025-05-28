/**
 * Program management module for the Academic Management System
 */

const Program = {
    // Current page for pagination
    currentPage: 0,

    // Page size for pagination
    pageSize: 10,

    // Total number of pages
    totalPages: 0,

    // Current program being edited
    currentProgram: null,

    // Current department ID filter
    currentDepartmentId: null,

    /**
     * Initialize program module
     */
    init: function() {
        this.bindEvents();
    },

    /**
     * Bind event listeners
     */
    bindEvents: function() {
        // Navigation link
        const programLink = document.querySelectorAll('[data-page="program"]');
        programLink.forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                this.showProgramSection();
            });
        });
    },

    /**
     * Show program section
     */
    showProgramSection: function() {
        // Hide other sections
        document.getElementById('dashboard-container').classList.add('hidden');
        document.getElementById('college-container').classList.add('hidden');
        document.getElementById('faculty-container').classList.add('hidden');
        document.getElementById('department-container').classList.add('hidden');

        // Show program section
        const programContainer = document.getElementById('program-container');
        programContainer.classList.remove('hidden');

        // Reset department filter
        this.currentDepartmentId = null;

        // Render program section
        this.renderProgramSection();
    },

    /**
     * Show programs by department
     * @param {number} departmentId - Department ID
     */
    showProgramsByDepartment: function(departmentId) {
        // Hide other sections
        document.getElementById('dashboard-container').classList.add('hidden');
        document.getElementById('college-container').classList.add('hidden');
        document.getElementById('faculty-container').classList.add('hidden');
        document.getElementById('department-container').classList.add('hidden');

        // Show program section
        const programContainer = document.getElementById('program-container');
        programContainer.classList.remove('hidden');

        // Set department filter
        this.currentDepartmentId = departmentId;

        // Render program section
        this.renderProgramSection();
    },

    /**
     * Render program section
     */
    renderProgramSection: function() {
        const programContainer = document.getElementById('program-container');
        programContainer.innerHTML = '';

        // Create section header
        const header = document.createElement('div');
        header.className = 'flex justify-between items-center mb-6';

        let headerTitle = 'Programs';
        if (this.currentDepartmentId) {
            headerTitle += ' by Department';
        }

        header.innerHTML = `
            <h1 class="text-3xl font-bold text-gray-800">${headerTitle}</h1>
            <div class="flex space-x-2">
                <button id="create-program-btn" class="btn btn-primary">
                    <i class="fas fa-plus mr-2"></i> Add Program
                </button>
            </div>
        `;
        programContainer.appendChild(header);

        // Create filters
        if (!this.currentDepartmentId) {
            const filters = document.createElement('div');
            filters.className = 'mb-6 flex flex-wrap gap-4';
            filters.innerHTML = `
                <div class="w-full md:w-auto">
                    <label for="department-filter" class="block text-sm font-medium text-gray-700 mb-1">Filter by Department</label>
                    <select id="department-filter" class="form-input">
                        <option value="">All Departments</option>
                        <!-- Department options will be added here -->
                    </select>
                </div>
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
            programContainer.appendChild(filters);

            // Load filter options
            this.loadFilterOptions();
        }

        // Create search bar
        const searchBar = document.createElement('div');
        searchBar.className = 'mb-6';
        searchBar.innerHTML = `
            <div class="flex">
                <input type="text" id="program-search" placeholder="Search programs..." class="form-input rounded-r-none">
                <button id="program-search-btn" class="btn btn-primary rounded-l-none">Search</button>
            </div>
        `;
        programContainer.appendChild(searchBar);

        // Create programs table
        const tableContainer = document.createElement('div');
        tableContainer.className = 'bg-white rounded-lg shadow-md overflow-hidden';
        tableContainer.innerHTML = `
            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Program Name</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Department</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Faculty</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                        </tr>
                    </thead>
                    <tbody id="programs-table-body" class="bg-white divide-y divide-gray-200">
                        <tr>
                            <td colspan="5" class="px-6 py-4 text-center text-gray-500">Loading programs...</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div id="program-pagination" class="px-6 py-4 bg-gray-50 flex justify-center">
                <!-- Pagination will be added here -->
            </div>
        `;
        programContainer.appendChild(tableContainer);

        // Add event listeners
        document.getElementById('create-program-btn').addEventListener('click', this.showCreateProgramForm.bind(this));
        document.getElementById('program-search-btn').addEventListener('click', this.handleProgramSearch.bind(this));
        document.getElementById('program-search').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.handleProgramSearch();
            }
        });

        // Add filter event listeners
        if (!this.currentDepartmentId) {
            document.getElementById('department-filter').addEventListener('change', this.handleDepartmentFilter.bind(this));
            document.getElementById('faculty-filter').addEventListener('change', this.handleFacultyFilter.bind(this));
            document.getElementById('college-filter').addEventListener('change', this.handleCollegeFilter.bind(this));
        }

        // Load programs
        if (this.currentDepartmentId) {
            this.loadProgramsByDepartment(this.currentDepartmentId);
        } else {
            this.loadPrograms();
        }
    },

    /**
     * Load filter options
     */
    loadFilterOptions: async function() {
        try {
            Utils.showLoading();

            // Load departments, faculties, and colleges
            const [departments, faculties, colleges] = await Promise.all([
                API.department.getAll(),
                API.faculty.getAll(),
                API.college.getAll()
            ]);

            Utils.hideLoading();

            // Populate department filter
            const departmentFilter = document.getElementById('department-filter');
            departments.forEach(department => {
                const option = document.createElement('option');
                option.value = department.id;
                option.textContent = department.departmentName;
                departmentFilter.appendChild(option);
            });

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
     * Load programs with pagination
     */
    loadPrograms: async function() {
        try {
            Utils.showLoading();

            const response = await API.program.getPaginated(this.currentPage, this.pageSize);

            Utils.hideLoading();

            // Update pagination info
            this.totalPages = response.totalPages;

            // Render programs
            this.renderPrograms(response.content);

            // Render pagination
            this.renderPagination();
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to load programs');
            console.error('Error loading programs:', error);
        }
    },

    /**
     * Load programs by department
     * @param {number} departmentId - Department ID
     */
    loadProgramsByDepartment: async function(departmentId) {
        try {
            Utils.showLoading();

            const programs = await API.program.getByDepartmentId(departmentId);

            Utils.hideLoading();

            // Render programs without pagination
            this.renderPrograms(programs);

            // Hide pagination
            document.getElementById('program-pagination').innerHTML = '';
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to load programs');
            console.error('Error loading programs by department:', error);
        }
    },

    /**
     * Render programs in table
     * @param {Array} programs - Array of program objects
     */
    renderPrograms: function(programs) {
        const tableBody = document.getElementById('programs-table-body');

        if (!programs || programs.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="5" class="px-6 py-4 text-center text-gray-500">No programs found</td>
                </tr>
            `;
            return;
        }

        tableBody.innerHTML = '';

        programs.forEach(program => {
            const row = document.createElement('tr');
            row.className = 'hover:bg-gray-50';
            row.innerHTML = `
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${program.id}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${program.programName}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${program.departmentName || 'N/A'}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${program.facultyName || 'N/A'}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    <button class="text-primary hover:text-primary-dark mr-2 edit-program" data-id="${program.id}">Edit</button>
                    <button class="text-red-600 hover:text-red-800 delete-program" data-id="${program.id}">Delete</button>
                    <button class="text-blue-600 hover:text-blue-800 ml-2 view-courses" data-id="${program.id}">View Courses</button>
                </td>
            `;
            tableBody.appendChild(row);

            // Add event listeners
            row.querySelector('.edit-program').addEventListener('click', () => this.handleEditProgram(program.id));
            row.querySelector('.delete-program').addEventListener('click', () => this.handleDeleteProgram(program.id));
            row.querySelector('.view-courses').addEventListener('click', () => this.handleViewCourses(program.id));
        });
    },

    /**
     * Render pagination controls
     */
    renderPagination: function() {
        const paginationContainer = document.getElementById('program-pagination');
        paginationContainer.innerHTML = '';

        if (this.totalPages <= 1) {
            return;
        }

        const pagination = Utils.createPagination(this.currentPage, this.totalPages, (page) => {
            this.currentPage = page;
            this.loadPrograms();
        });

        paginationContainer.appendChild(pagination);
    },

    /**
     * Show create program form
     */
    showCreateProgramForm: async function() {
        try {
            Utils.showLoading();

            // Load departments for dropdown
            const departments = await API.department.getAll();

            Utils.hideLoading();

            let departmentOptions = '';
            departments.forEach(department => {
                departmentOptions += `<option value="${department.id}">${department.departmentName}</option>`;
            });

            Swal.fire({
                title: 'Create New Program',
                html: `
                    <div class="mb-4">
                        <label for="program-name" class="block text-sm font-medium text-gray-700 mb-1">Program Name</label>
                        <input id="program-name" class="form-input w-full" placeholder="Enter program name">
                    </div>
                    <div class="mb-4">
                        <label for="program-code" class="block text-sm font-medium text-gray-700 mb-1">Program Code</label>
                        <input id="program-code" class="form-input w-full" placeholder="Enter program code">
                    </div>
                    <div class="mb-4">
                        <label for="department-id" class="block text-sm font-medium text-gray-700 mb-1">Department</label>
                        <select id="department-id" class="form-input w-full">
                            <option value="">Select Department</option>
                            ${departmentOptions}
                        </select>
                    </div>
                `,
                showCancelButton: true,
                confirmButtonText: 'Create',
                confirmButtonColor: '#3B82F6',
                cancelButtonColor: '#6B7280',
                preConfirm: () => {
                    const programName = document.getElementById('program-name').value;
                    const programCode = document.getElementById('program-code').value;
                    const departmentId = document.getElementById('department-id').value;

                    if (!programName) {
                        Swal.showValidationMessage('Program name is required');
                        return false;
                    }

                    if (!programCode) {
                        Swal.showValidationMessage('Program code is required');
                        return false;
                    }

                    if (!departmentId) {
                        Swal.showValidationMessage('Department is required');
                        return false;
                    }

                    return {
                        programName,
                        programCode,
                        departmentId: parseInt(departmentId)
                    };
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    this.createProgram(result.value);
                }
            });

            // Set default department if filtering by department
            if (this.currentDepartmentId) {
                document.getElementById('department-id').value = this.currentDepartmentId;
            }
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to load departments');
        }
    },

    /**
     * Create new program
     * @param {Object} programData - Program data
     */
    createProgram: async function(programData) {
        try {
            Utils.showLoading();
            await API.program.create(programData);
            Utils.hideLoading();
            Utils.showSuccess('Program created successfully');

            if (this.currentDepartmentId) {
                this.loadProgramsByDepartment(this.currentDepartmentId);
            } else {
                this.loadPrograms();
            }
        } catch (error) {
            Utils.hideLoading();
            Utils.showError(error.message || 'Failed to create program');
        }
    },

    /**
     * Handle edit program
     * @param {number} programId - Program ID
     */
    handleEditProgram: async function(programId) {
        try {
            Utils.showLoading();

            // Load program and departments
            const [program, departments] = await Promise.all([
                API.program.getById(programId),
                API.department.getAll()
            ]);

            Utils.hideLoading();

            this.currentProgram = program;

            let departmentOptions = '';
            departments.forEach(department => {
                const selected = program.department && program.department.id === department.id ? 'selected' : '';
                departmentOptions += `<option value="${department.id}" ${selected}>${department.departmentName}</option>`;
            });

            Swal.fire({
                title: 'Edit Program',
                html: `
                    <div class="mb-4">
                        <label for="edit-program-name" class="block text-sm font-medium text-gray-700 mb-1">Program Name</label>
                        <input id="edit-program-name" class="form-input w-full" value="${program.programName}">
                    </div>
                    <div class="mb-4">
                        <label for="edit-program-code" class="block text-sm font-medium text-gray-700 mb-1">Program Code</label>
                        <input id="edit-program-code" class="form-input w-full" value="${program.programCode || ''}">
                    </div>
                    <div class="mb-4">
                        <label for="edit-department-id" class="block text-sm font-medium text-gray-700 mb-1">Department</label>
                        <select id="edit-department-id" class="form-input w-full">
                            <option value="">Select Department</option>
                            ${departmentOptions}
                        </select>
                    </div>
                `,
                showCancelButton: true,
                confirmButtonText: 'Update',
                confirmButtonColor: '#3B82F6',
                cancelButtonColor: '#6B7280',
                preConfirm: () => {
                    const programName = document.getElementById('edit-program-name').value;
                    const programCode = document.getElementById('edit-program-code').value;
                    const departmentId = document.getElementById('edit-department-id').value;

                    if (!programName) {
                        Swal.showValidationMessage('Program name is required');
                        return false;
                    }

                    if (!programCode) {
                        Swal.showValidationMessage('Program code is required');
                        return false;
                    }

                    if (!departmentId) {
                        Swal.showValidationMessage('Department is required');
                        return false;
                    }

                    return {
                        programName,
                        programCode,
                        departmentId: parseInt(departmentId)
                    };
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    this.updateProgram(programId, result.value);
                }
            });
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to load program details');
        }
    },

    /**
     * Update program
     * @param {number} programId - Program ID
     * @param {Object} programData - Updated program data
     */
    updateProgram: async function(programId, programData) {
        try {
            Utils.showLoading();
            await API.program.update(programId, programData);
            Utils.hideLoading();
            Utils.showSuccess('Program updated successfully');

            if (this.currentDepartmentId) {
                this.loadProgramsByDepartment(this.currentDepartmentId);
            } else {
                this.loadPrograms();
            }
        } catch (error) {
            Utils.hideLoading();
            Utils.showError(error.message || 'Failed to update program');
        }
    },

    /**
     * Handle delete program
     * @param {number} programId - Program ID
     */
    handleDeleteProgram: function(programId) {
        Utils.showConfirm(
            'Delete Program',
            'Are you sure you want to delete this program? This action cannot be undone.',
            'Delete',
            async () => {
                try {
                    Utils.showLoading();
                    await API.program.delete(programId);
                    Utils.hideLoading();
                    Utils.showSuccess('Program deleted successfully');

                    if (this.currentDepartmentId) {
                        this.loadProgramsByDepartment(this.currentDepartmentId);
                    } else {
                        this.loadPrograms();
                    }
                } catch (error) {
                    Utils.hideLoading();
                    Utils.showError(error.message || 'Failed to delete program');
                }
            }
        );
    },

    /**
     * Handle program search
     */
    handleProgramSearch: async function() {
        const searchTerm = document.getElementById('program-search').value.trim();

        if (!searchTerm) {
            if (this.currentDepartmentId) {
                this.loadProgramsByDepartment(this.currentDepartmentId);
            } else {
                this.currentPage = 0;
                this.loadPrograms();
            }
            return;
        }

        try {
            Utils.showLoading();
            const programs = await API.program.search(searchTerm);
            Utils.hideLoading();

            // Render programs without pagination
            this.renderPrograms(programs);

            // Hide pagination
            document.getElementById('program-pagination').innerHTML = '';
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to search programs');
        }
    },

    /**
     * Handle department filter change
     */
    handleDepartmentFilter: function() {
        const departmentId = document.getElementById('department-filter').value;

        if (departmentId) {
            this.loadProgramsByDepartment(departmentId);
        } else {
            this.currentPage = 0;
            this.loadPrograms();
        }
    },

    /**
     * Handle faculty filter change
     */
    handleFacultyFilter: async function() {
        const facultyId = document.getElementById('faculty-filter').value;

        if (!facultyId) {
            this.currentPage = 0;
            this.loadPrograms();
            return;
        }

        try {
            Utils.showLoading();
            const programs = await API.program.getByFacultyId(facultyId);
            Utils.hideLoading();

            // Render programs without pagination
            this.renderPrograms(programs);

            // Hide pagination
            document.getElementById('program-pagination').innerHTML = '';
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to filter programs by faculty');
        }
    },

    /**
     * Handle college filter change
     */
    handleCollegeFilter: async function() {
        const collegeId = document.getElementById('college-filter').value;

        if (!collegeId) {
            this.currentPage = 0;
            this.loadPrograms();
            return;
        }

        try {
            Utils.showLoading();
            const programs = await API.program.getByCollegeId(collegeId);
            Utils.hideLoading();

            // Render programs without pagination
            this.renderPrograms(programs);

            // Hide pagination
            document.getElementById('program-pagination').innerHTML = '';
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to filter programs by college');
        }
    }
};

// Initialize program module when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    Program.init();
});

// Export Program object
window.Program = Program;
