/**
 * Course management module for the Academic Management System
 */

const Course = {
    // Current page for pagination
    currentPage: 0,
    
    // Page size for pagination
    pageSize: 10,
    
    // Total number of pages
    totalPages: 0,
    
    // Current course being edited
    currentCourse: null,

    /**
     * Initialize course module
     */
    init: function() {
        console.log("Course module initialized");
        this.bindEvents();
    },

    /**
     * Bind event listeners
     */
    bindEvents: function() {
        // Navigation link
        const courseLinks = document.querySelectorAll('[data-page="course"]');
        courseLinks.forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                console.log("Course link clicked");
                this.showCourseSection();
            });
        });
    },

    /**
     * Show course section
     */
    showCourseSection: function() {
        console.log("Showing course section");
        // Hide other sections
        document.getElementById('dashboard-container').classList.add('hidden');
        document.getElementById('college-container').classList.add('hidden');
        document.getElementById('faculty-container').classList.add('hidden');
        document.getElementById('department-container').classList.add('hidden');
        document.getElementById('program-container').classList.add('hidden');
        
        // Show course section
        const courseContainer = document.getElementById('course-container');
        courseContainer.classList.remove('hidden');
        
        // Render course section
        this.renderCourseSection();
    },

    /**
     * Render course section
     */
    renderCourseSection: function() {
        console.log("Rendering course section");
        const courseContainer = document.getElementById('course-container');
        courseContainer.innerHTML = '';
        
        // Create section header
        const header = document.createElement('div');
        header.className = 'flex justify-between items-center mb-6';
        header.innerHTML = `
            <h1 class="text-3xl font-bold text-gray-800">Courses</h1>
            <div class="flex space-x-2">
                <button id="create-course-btn" class="bg-primary hover:bg-blue-700 text-white px-4 py-2 rounded">
                    <i class="fas fa-plus mr-2"></i> Add Course
                </button>
            </div>
        `;
        courseContainer.appendChild(header);
        
        // Create search and filter section
        const filterSection = document.createElement('div');
        filterSection.className = 'bg-white rounded-lg shadow-md p-4 mb-6';
        filterSection.innerHTML = `
            <div class="flex flex-col md:flex-row md:items-center md:justify-between space-y-4 md:space-y-0">
                <div class="flex flex-col md:flex-row md:items-center space-y-4 md:space-y-0 md:space-x-4">
                    <div>
                        <label for="department-filter" class="block text-sm font-medium text-gray-700 mb-1">Department</label>
                        <select id="department-filter" class="rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                            <option value="">All Departments</option>
                            <!-- Departments will be loaded dynamically -->
                        </select>
                    </div>
                    <div>
                        <label for="semester-filter" class="block text-sm font-medium text-gray-700 mb-1">Semester</label>
                        <select id="semester-filter" class="rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                            <option value="">All Semesters</option>
                            <option value="SEMESTER_1">Semester 1</option>
                            <option value="SEMESTER_2">Semester 2</option>
                            <option value="SEMESTER_3">Semester 3</option>
                            <option value="SEMESTER_4">Semester 4</option>
                            <option value="SEMESTER_5">Semester 5</option>
                            <option value="SEMESTER_6">Semester 6</option>
                            <option value="SEMESTER_7">Semester 7</option>
                            <option value="SEMESTER_8">Semester 8</option>
                        </select>
                    </div>
                    <div class="md:self-end">
                        <button id="course-filter-btn" class="bg-gray-200 hover:bg-gray-300 text-gray-800 px-4 py-2 rounded">
                            Apply Filters
                        </button>
                    </div>
                </div>
                <div class="flex items-center space-x-2">
                    <div>
                        <input id="course-search" type="text" placeholder="Search courses..." class="rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                    </div>
                    <button id="course-search-btn" class="bg-primary hover:bg-blue-700 text-white px-4 py-2 rounded">
                        <i class="fas fa-search mr-2"></i> Search
                    </button>
                </div>
            </div>
        `;
        courseContainer.appendChild(filterSection);
        
        // Create courses table
        const tableContainer = document.createElement('div');
        tableContainer.className = 'bg-white rounded-lg shadow-md overflow-hidden';
        tableContainer.innerHTML = `
            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Course Code</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Course Name</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Department</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Semester</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Credit Hours</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                        </tr>
                    </thead>
                    <tbody id="courses-table-body" class="bg-white divide-y divide-gray-200">
                        <!-- Courses will be loaded here -->
                    </tbody>
                </table>
            </div>
            <div id="course-pagination" class="px-6 py-4 bg-gray-50 flex justify-center">
                <!-- Pagination will be added here -->
            </div>
        `;
        courseContainer.appendChild(tableContainer);
        
        // Add event listeners
        document.getElementById('create-course-btn').addEventListener('click', this.showCreateCourseForm.bind(this));
        document.getElementById('course-search-btn').addEventListener('click', this.handleCourseSearch.bind(this));
        document.getElementById('course-search').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.handleCourseSearch();
            }
        });
        document.getElementById('course-filter-btn').addEventListener('click', this.handleCourseFilter.bind(this));
        
        // Load departments for filter
        this.loadDepartmentsForFilter();
        
        // Show loading indicator
        const tableBody = document.getElementById('courses-table-body');
        tableBody.innerHTML = `
            <tr>
                <td colspan="7" class="px-6 py-4 text-center text-gray-500">
                    <div class="flex justify-center items-center space-x-2">
                        <svg class="animate-spin h-5 w-5 text-primary" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                        </svg>
                        <span>Loading courses...</span>
                    </div>
                </td>
            </tr>
        `;
        
        // Load courses
        this.loadCourses();
    },

    /**
     * Load departments for filter dropdown
     */
    loadDepartmentsForFilter: async function() {
        try {
            const departments = await API.department.getAll();
            
            const departmentFilter = document.getElementById('department-filter');
            departmentFilter.innerHTML = '<option value="">All Departments</option>';
            
            departments.forEach(department => {
                const option = document.createElement('option');
                option.value = department.id;
                option.textContent = department.departmentName;
                departmentFilter.appendChild(option);
            });
        } catch (error) {
            console.error('Error loading departments for filter:', error);
        }
    },

    /**
     * Load courses with pagination
     */
    loadCourses: async function() {
        try {
            Utils.showLoading();
            
            console.log("Loading courses for page:", this.currentPage);
            const response = await API.course.getPaginated(this.currentPage, this.pageSize);
            
            console.log("Courses API response:", response);
            
            Utils.hideLoading();
            
            // Check if response has the expected structure
            if (!response || !response.success) {
                console.error("Invalid response format:", response);
                this.renderEmptyCourses("Error: Unexpected API response format");
                return;
            }
            
            // Handle the response data structure correctly
            const courses = response.data.content || [];
            this.totalPages = response.data.totalPages || 0;
            
            // Render courses
            this.renderCourses(courses);
            
            // Render pagination
            this.renderPagination();
        } catch (error) {
            Utils.hideLoading();
            console.error('Error loading courses:', error);
            this.renderEmptyCourses(`Failed to load courses: ${error.message || 'Unknown error'}`);
        }
    },

    /**
     * Render empty courses table with message
     * @param {string} message - Message to display
     */
    renderEmptyCourses: function(message) {
        const tableBody = document.getElementById('courses-table-body');
        tableBody.innerHTML = `
            <tr>
                <td colspan="7" class="px-6 py-4 text-center text-gray-500">
                    ${message || 'No courses found'}
                </td>
            </tr>
        `;
        
        // Clear pagination
        document.getElementById('course-pagination').innerHTML = '';
    },

    /**
     * Render courses in the table
     * @param {Array} courses - Array of course objects
     */
    renderCourses: function(courses) {
        console.log("Rendering courses:", courses);
        const tableBody = document.getElementById('courses-table-body');
        tableBody.innerHTML = '';
        
        if (!courses || courses.length === 0) {
            this.renderEmptyCourses();
            return;
        }
        
        courses.forEach(course => {
            const row = document.createElement('tr');
            row.className = 'hover:bg-gray-50';
            row.innerHTML = `
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${course.id || 'N/A'}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${course.courseCode || 'N/A'}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${course.courseName || 'N/A'}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${course.departmentName || 'N/A'}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${this.formatSemester(course.semester) || 'N/A'}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${course.creditHours || 'N/A'}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    <button class="text-primary hover:text-primary-dark mr-2 edit-course" data-id="${course.id}">Edit</button>
                    <button class="text-red-600 hover:text-red-800 delete-course" data-id="${course.id}">Delete</button>
                </td>
            `;
            tableBody.appendChild(row);
            
            // Add event listeners
            row.querySelector('.edit-course').addEventListener('click', () => this.handleEditCourse(course.id));
            row.querySelector('.delete-course').addEventListener('click', () => this.handleDeleteCourse(course.id));
        });
    },

    /**
     * Format semester for display
     * @param {string} semester - Semester value
     * @returns {string} Formatted semester
     */
    formatSemester: function(semester) {
        if (!semester) return 'N/A';
        
        // Convert SEMESTER_1 to "Semester 1"
        return semester.replace('SEMESTER_', 'Semester ');
    },

    /**
     * Show create course form
     */
    showCreateCourseForm: async function() {
        try {
            Utils.showLoading();
            
            // Load departments for dropdown
            const departments = await API.department.getAll();
            
            Utils.hideLoading();
            
            let departmentOptions = '<option value="">Select Department</option>';
            departments.forEach(department => {
                departmentOptions += `<option value="${department.id}">${department.departmentName}</option>`;
            });
            
            Swal.fire({
                title: 'Create New Course',
                html: `
                    <div class="mb-4">
                        <label for="course-code" class="block text-sm font-medium text-gray-700 mb-1">Course Code</label>
                        <input id="course-code" class="form-input w-full" placeholder="Enter course code">
                    </div>
                    <div class="mb-4">
                        <label for="course-name" class="block text-sm font-medium text-gray-700 mb-1">Course Name</label>
                        <input id="course-name" class="form-input w-full" placeholder="Enter course name">
                    </div>
                    <div class="mb-4">
                        <label for="department-id" class="block text-sm font-medium text-gray-700 mb-1">Department</label>
                        <select id="department-id" class="form-select w-full">
                            ${departmentOptions}
                        </select>
                    </div>
                    <div class="mb-4">
                        <label for="semester" class="block text-sm font-medium text-gray-700 mb-1">Semester</label>
                        <select id="semester" class="form-select w-full">
                            <option value="">Select Semester</option>
                            <option value="SEMESTER_1">Semester 1</option>
                            <option value="SEMESTER_2">Semester 2</option>
                            <option value="SEMESTER_3">Semester 3</option>
                            <option value="SEMESTER_4">Semester 4</option>
                            <option value="SEMESTER_5">Semester 5</option>
                            <option value="SEMESTER_6">Semester 6</option>
                            <option value="SEMESTER_7">Semester 7</option>
                            <option value="SEMESTER_8">Semester 8</option>
                        </select>
                    </div>
                    <div class="mb-4">
                        <label for="credit-hours" class="block text-sm font-medium text-gray-700 mb-1">Credit Hours</label>
                        <input id="credit-hours" type="number" min="1" max="6" class="form-input w-full" placeholder="Enter credit hours">
                    </div>
                    <div class="mb-4">
                        <label for="course-description" class="block text-sm font-medium text-gray-700 mb-1">Description</label>
                        <textarea id="course-description" class="form-textarea w-full" rows="3" placeholder="Enter course description"></textarea>
                    </div>
                `,
                showCancelButton: true,
                confirmButtonText: 'Create',
                confirmButtonColor: '#3B82F6',
                cancelButtonColor: '#6B7280',
                preConfirm: () => {
                    const courseCode = document.getElementById('course-code').value;
                    const courseName = document.getElementById('course-name').value;
                    const departmentId = document.getElementById('department-id').value;
                    const semester = document.getElementById('semester').value;
                    const creditHours = document.getElementById('credit-hours').value;
                    const courseDescription = document.getElementById('course-description').value;
                    
                    if (!courseCode) {
                        Swal.showValidationMessage('Course code is required');
                        return false;
                    }
                    
                    if (!courseName) {
                        Swal.showValidationMessage('Course name is required');
                        return false;
                    }
                    
                    if (!departmentId) {
                        Swal.showValidationMessage('Department is required');
                        return false;
                    }
                    
                    if (!semester) {
                        Swal.showValidationMessage('Semester is required');
                        return false;
                    }
                    
                    if (!creditHours) {
                        Swal.showValidationMessage('Credit hours is required');
                        return false;
                    }
                    
                    return {
                        courseCode,
                        courseName,
                        departmentId: parseInt(departmentId),
                        semester,
                        creditHours: parseInt(creditHours),
                        courseDescription
                    };
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    this.createCourse(result.value);
                }
            });
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to load departments');
        }
    },

    /**
     * Create new course
     * @param {Object} courseData - Course data
     */
    createCourse: async function(courseData) {
        try {
            Utils.showLoading();
            await API.course.create(courseData);
            Utils.hideLoading();
            Utils.showSuccess('Course created successfully');
            this.loadCourses();
        } catch (error) {
            Utils.hideLoading();
            Utils.showError(error.message || 'Failed to create course');
        }
    },

    /**
     * Handle edit course
     * @param {number} courseId - Course ID
     */
    handleEditCourse: async function(courseId) {
        try {
            Utils.showLoading();
            
            // Load course and departments
            const [course, departments] = await Promise.all([
                API.course.getById(courseId),
                API.department.getAll()
            ]);
            
            Utils.hideLoading();
            
            this.currentCourse = course;
            
            let departmentOptions = '<option value="">Select Department</option>';
            departments.forEach(department => {
                const selected = course.department && course.department.id === department.id ? 'selected' : '';
                departmentOptions += `<option value="${department.id}" ${selected}>${department.departmentName}</option>`;
            });
            
            Swal.fire({
                title: 'Edit Course',
                html: `
                    <div class="mb-4">
                        <label for="edit-course-code" class="block text-sm font-medium text-gray-700 mb-1">Course Code</label>
                        <input id="edit-course-code" class="form-input w-full" value="${course.courseCode}">
                    </div>
                    <div class="mb-4">
                        <label for="edit-course-name" class="block text-sm font-medium text-gray-700 mb-1">Course Name</label>
                        <input id="edit-course-name" class="form-input w-full" value="${course.courseName}">
                    </div>
                    <div class="mb-4">
                        <label for="edit-department-id" class="block text-sm font-medium text-gray-700 mb-1">Department</label>
                        <select id="edit-department-id" class="form-select w-full">
                            ${departmentOptions}
                        </select>
                    </div>
                    <div class="mb-4">
                        <label for="edit-semester" class="block text-sm font-medium text-gray-700 mb-1">Semester</label>
                        <select id="edit-semester" class="form-select w-full">
                            <option value="">Select Semester</option>
                            <option value="SEMESTER_1" ${course.semester === 'SEMESTER_1' ? 'selected' : ''}>Semester 1</option>
                            <option value="SEMESTER_2" ${course.semester === 'SEMESTER_2' ? 'selected' : ''}>Semester 2</option>
                            <option value="SEMESTER_3" ${course.semester === 'SEMESTER_3' ? 'selected' : ''}>Semester 3</option>
                            <option value="SEMESTER_4" ${course.semester === 'SEMESTER_4' ? 'selected' : ''}>Semester 4</option>
                            <option value="SEMESTER_5" ${course.semester === 'SEMESTER_5' ? 'selected' : ''}>Semester 5</option>
                            <option value="SEMESTER_6" ${course.semester === 'SEMESTER_6' ? 'selected' : ''}>Semester 6</option>
                            <option value="SEMESTER_7" ${course.semester === 'SEMESTER_7' ? 'selected' : ''}>Semester 7</option>
                            <option value="SEMESTER_8" ${course.semester === 'SEMESTER_8' ? 'selected' : ''}>Semester 8</option>
                        </select>
                    </div>
                    <div class="mb-4">
                        <label for="edit-credit-hours" class="block text-sm font-medium text-gray-700 mb-1">Credit Hours</label>
                        <input id="edit-credit-hours" type="number" min="1" max="6" class="form-input w-full" value="${course.creditHours}">
                    </div>
                    <div class="mb-4">
                        <label for="edit-course-description" class="block text-sm font-medium text-gray-700 mb-1">Description</label>
                        <textarea id="edit-course-description" class="form-textarea w-full" rows="3">${course.courseDescription || ''}</textarea>
                    </div>
                `,
                showCancelButton: true,
                confirmButtonText: 'Update',
                confirmButtonColor: '#3B82F6',
                cancelButtonColor: '#6B7280',
                preConfirm: () => {
                    const courseCode = document.getElementById('edit-course-code').value;
                    const courseName = document.getElementById('edit-course-name').value;
                    const departmentId = document.getElementById('edit-department-id').value;
                    const semester = document.getElementById('edit-semester').value;
                    const creditHours = document.getElementById('edit-credit-hours').value;
                    const courseDescription = document.getElementById('edit-course-description').value;
                    
                    if (!courseCode) {
                        Swal.showValidationMessage('Course code is required');
                        return false;
                    }
                    
                    if (!courseName) {
                        Swal.showValidationMessage('Course name is required');
                        return false;
                    }
                    
                    if (!departmentId) {
                        Swal.showValidationMessage('Department is required');
                        return false;
                    }
                    
                    if (!semester) {
                        Swal.showValidationMessage('Semester is required');
                        return false;
                    }
                    
                    if (!creditHours) {
                        Swal.showValidationMessage('Credit hours is required');
                        return false;
                    }
                    
                    return {
                        courseCode,
                        courseName,
                        departmentId: parseInt(departmentId),
                        semester,
                        creditHours: parseInt(creditHours),
                        courseDescription
                    };
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    this.updateCourse(courseId, result.value);
                }
            });
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to load course details');
        }
    },

    /**
     * Update course
     * @param {number} courseId - Course ID
     * @param {Object} courseData - Updated course data
     */
    updateCourse: async function(courseId, courseData) {
        try {
            Utils.showLoading();
            await API.course.update(courseId, courseData);
            Utils.hideLoading();
            Utils.showSuccess('Course updated successfully');
            this.loadCourses();
        } catch (error) {
            Utils.hideLoading();
            Utils.showError(error.message || 'Failed to update course');
        }
    },

    /**
     * Handle delete course
     * @param {number} courseId - Course ID
     */
    handleDeleteCourse: function(courseId) {
        Utils.showConfirm(
            'Delete Course',
            'Are you sure you want to delete this course? This action cannot be undone.',
            'Delete',
            async () => {
                try {
                    Utils.showLoading();
                    await API.course.delete(courseId);
                    Utils.hideLoading();
                    Utils.showSuccess('Course deleted successfully');
                    this.loadCourses();
                } catch (error) {
                    Utils.hideLoading();
                    Utils.showError(error.message || 'Failed to delete course');
                }
            }
        );
    },

    /**
     * Show courses by department
     * @param {number} departmentId - Department ID
     */
    showCoursesByDepartment: function(departmentId) {
        // Hide other sections
        document.getElementById('dashboard-container').classList.add('hidden');
        document.getElementById('college-container').classList.add('hidden');
        document.getElementById('faculty-container').classList.add('hidden');
        document.getElementById('department-container').classList.add('hidden');
        document.getElementById('program-container').classList.add('hidden');
        
        // Show course section
        const courseContainer = document.getElementById('course-container');
        courseContainer.classList.remove('hidden');
        
        // Set department filter
        this.currentDepartmentFilter = departmentId;
        this.currentSemesterFilter = '';
        this.currentSearchQuery = '';
        this.currentPage = 0;
        
        // Render course section
        this.renderCourseSection();
        
        // Set department filter dropdown value
        setTimeout(() => {
            const departmentFilter = document.getElementById('department-filter');
            if (departmentFilter) {
                departmentFilter.value = departmentId;
            }
        }, 100);
    },

    /**
     * Show courses by program
     * @param {number} programId - Program ID
     */
    showCoursesByProgram: async function(programId) {
        try {
            Utils.showLoading();
            
            // Get program with courses
            const program = await API.program.getWithCourses(programId);
            
            Utils.hideLoading();
            
            // Hide other sections
            document.getElementById('dashboard-container').classList.add('hidden');
            document.getElementById('college-container').classList.add('hidden');
            document.getElementById('faculty-container').classList.add('hidden');
            document.getElementById('department-container').classList.add('hidden');
            document.getElementById('program-container').classList.add('hidden');
            
            // Show course section
            const courseContainer = document.getElementById('course-container');
            courseContainer.classList.remove('hidden');
            
            // Render course section with program courses
            this.renderCourseSection();
            
            // Render program courses
            this.renderCourses(program.courses);
            
            // Hide pagination
            document.getElementById('course-pagination').innerHTML = '';
            
            // Add program info
            const programInfo = document.createElement('div');
            programInfo.className = 'bg-blue-50 p-4 rounded-lg mb-6';
            programInfo.innerHTML = `
                <h2 class="text-xl font-semibold mb-2">Courses for Program: ${program.programName}</h2>
                <p>Department: ${program.department ? program.department.departmentName : 'N/A'}</p>
            `;
            
            courseContainer.insertBefore(programInfo, courseContainer.querySelector('.bg-white.rounded-lg.shadow-md.overflow-hidden'));
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to load program courses');
        }
    },

    /**
     * Get course count for dashboard
     * @returns {Promise<number>} Promise resolving to course count
     */
    getCourseCount: async function() {
        try {
            const response = await API.course.getAll();
            return response.data.length;
        } catch (error) {
            console.error('Error getting course count:', error);
            return 0;
        }
    },

    /**
     * Handle course search
     */
    handleCourseSearch: function() {
        const searchTerm = document.getElementById('course-search').value.trim();
        console.log("Searching for courses with term:", searchTerm);
        
        if (!searchTerm) {
            // If search term is empty, load all courses
            this.loadCourses();
            return;
        }
        
        // Filter courses by search term
        this.searchCourses(searchTerm);
    },

    /**
     * Search courses by term
     * @param {string} searchTerm - Search term
     */
    searchCourses: async function(searchTerm) {
        try {
            Utils.showLoading();
            
            // For now, we'll get all courses and filter client-side
            // In the future, this could be replaced with a server-side search
            const response = await API.course.getAll();
            
            Utils.hideLoading();
            
            if (!response || !response.success) {
                console.error("Invalid response format:", response);
                this.renderEmptyCourses("Error: Unexpected API response format");
                return;
            }
            
            const courses = response.data || [];
            
            // Filter courses by search term (case-insensitive)
            const filteredCourses = courses.filter(course => 
                (course.courseCode && course.courseCode.toLowerCase().includes(searchTerm.toLowerCase())) ||
                (course.courseName && course.courseName.toLowerCase().includes(searchTerm.toLowerCase())) ||
                (course.departmentName && course.departmentName.toLowerCase().includes(searchTerm.toLowerCase()))
            );
            
            // Render filtered courses
            this.renderCourses(filteredCourses);
            
            // Clear pagination since we're showing filtered results
            document.getElementById('course-pagination').innerHTML = '';
            
            // Show message if no results
            if (filteredCourses.length === 0) {
                this.renderEmptyCourses(`No courses found matching "${searchTerm}"`);
            }
        } catch (error) {
            Utils.hideLoading();
            console.error('Error searching courses:', error);
            this.renderEmptyCourses(`Failed to search courses: ${error.message || 'Unknown error'}`);
        }
    },

    /**
     * Handle course filter
     */
    handleCourseFilter: function() {
        const departmentId = document.getElementById('department-filter').value;
        const semester = document.getElementById('semester-filter').value;

        console.log("Filtering courses by department:", departmentId, "and semester:", semester);

        if (!departmentId && !semester) {
            // If no filters are applied, load all courses
            this.loadCourses();
            return;
        }

        // Apply filters
        this.filterCourses(departmentId, semester);
    },

    /**
     * Filter courses by department and semester
     * @param {string} departmentId - Department ID
     * @param {string} semester - Semester
     */
    filterCourses: async function(departmentId, semester) {
        try {
            Utils.showLoading();

            let response;

            if (departmentId && semester) {
                response = await API.course.getFiltered(departmentId, semester);
            } else if (departmentId) {
                response = await API.course.getByDepartment(departmentId);
            } else if (semester) {
                response = await API.course.getBySemester(semester);
            } else {
                response = await API.course.getAll();
            }

            console.log("Filtered courses response:", response);

            Utils.hideLoading();

            if (!response || !response.success) {
                console.error("Invalid response format:", response);
                this.renderEmptyCourses("Error: Unexpected API response format");
                return;
            }

            const courses = response.data || [];
            this.totalPages = response.totalPages || 0;

            this.renderCourses(courses);
            this.renderPagination();
        } catch (error) {
            Utils.hideLoading();
            console.error('Error filtering courses:', error);
            this.renderEmptyCourses(`Failed to filter courses: ${error.message || 'Unknown error'}`);
        }
    },

    /**
     * Render pagination controls
     */
    renderPagination: function() {
        const paginationContainer = document.getElementById('course-pagination');
        paginationContainer.innerHTML = '';
        
        if (this.totalPages <= 1) {
            return;
        }
        
        const pagination = Utils.createPagination(this.currentPage, this.totalPages, (page) => {
            this.currentPage = page;
            this.loadCourses();
        });
        
        paginationContainer.appendChild(pagination);
    },
};

/**
 * Course management module for the Academic Management System
 */

const Course = {
    // Current page for pagination
    currentPage: 0,
    
    // Page size for pagination
    pageSize: 10,
    
    // Total number of pages
    totalPages: 0,
    
    // Current course being edited
    currentCourse: null,

    /**
     * Initialize course module
     */
    init: function() {
        console.log("Course module initialized");
        this.bindEvents();
    },

    /**
     * Bind event listeners
     */
    bindEvents: function() {
        // Navigation link
        const courseLinks = document.querySelectorAll('[data-page="course"]');
        courseLinks.forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                console.log("Course link clicked");
                this.showCourseSection();
            });
        });
    },

    /**
     * Show course section
     */
    showCourseSection: function() {
        console.log("Showing course section");
        // Hide other sections
        document.getElementById('dashboard-container').classList.add('hidden');
        document.getElementById('college-container').classList.add('hidden');
        document.getElementById('faculty-container').classList.add('hidden');
        document.getElementById('department-container').classList.add('hidden');
        document.getElementById('program-container').classList.add('hidden');
        
        // Show course section
        const courseContainer = document.getElementById('course-container');
        courseContainer.classList.remove('hidden');
        
        // Render course section
        this.renderCourseSection();
    },

    /**
     * Render course section
     */
    renderCourseSection: function() {
        console.log("Rendering course section");
        const courseContainer = document.getElementById('course-container');
        courseContainer.innerHTML = '';
        
        // Create section header
        const header = document.createElement('div');
        header.className = 'flex justify-between items-center mb-6';
        header.innerHTML = `
            <h1 class="text-3xl font-bold text-gray-800">Courses</h1>
            <div class="flex space-x-2">
                <button id="create-course-btn" class="bg-primary hover:bg-blue-700 text-white px-4 py-2 rounded">
                    <i class="fas fa-plus mr-2"></i> Add Course
                </button>
            </div>
        `;
        courseContainer.appendChild(header);
        
        // Create search and filter section
        const filterSection = document.createElement('div');
        filterSection.className = 'bg-white rounded-lg shadow-md p-4 mb-6';
        filterSection.innerHTML = `
            <div class="flex flex-col md:flex-row md:items-center md:justify-between space-y-4 md:space-y-0">
                <div class="flex flex-col md:flex-row md:items-center space-y-4 md:space-y-0 md:space-x-4">
                    <div>
                        <label for="department-filter" class="block text-sm font-medium text-gray-700 mb-1">Department</label>
                        <select id="department-filter" class="rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                            <option value="">All Departments</option>
                            <!-- Departments will be loaded dynamically -->
                        </select>
                    </div>
                    <div>
                        <label for="semester-filter" class="block text-sm font-medium text-gray-700 mb-1">Semester</label>
                        <select id="semester-filter" class="rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                            <option value="">All Semesters</option>
                            <option value="SEMESTER_1">Semester 1</option>
                            <option value="SEMESTER_2">Semester 2</option>
                            <option value="SEMESTER_3">Semester 3</option>
                            <option value="SEMESTER_4">Semester 4</option>
                            <option value="SEMESTER_5">Semester 5</option>
                            <option value="SEMESTER_6">Semester 6</option>
                            <option value="SEMESTER_7">Semester 7</option>
                            <option value="SEMESTER_8">Semester 8</option>
                        </select>
                    </div>
                    <div class="md:self-end">
                        <button id="course-filter-btn" class="bg-gray-200 hover:bg-gray-300 text-gray-800 px-4 py-2 rounded">
                            Apply Filters
                        </button>
                    </div>
                </div>
                <div class="flex items-center space-x-2">
                    <div>
                        <input id="course-search" type="text" placeholder="Search courses..." class="rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                    </div>
                    <button id="course-search-btn" class="bg-primary hover:bg-blue-700 text-white px-4 py-2 rounded">
                        <i class="fas fa-search mr-2"></i> Search
                    </button>
                </div>
            </div>
        `;
        courseContainer.appendChild(filterSection);
        
        // Create courses table
        const tableContainer = document.createElement('div');
        tableContainer.className = 'bg-white rounded-lg shadow-md overflow-hidden';
        tableContainer.innerHTML = `
            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Course Code</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Course Name</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Department</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Semester</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Credit Hours</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                        </tr>
                    </thead>
                    <tbody id="courses-table-body" class="bg-white divide-y divide-gray-200">
                        <!-- Courses will be loaded here -->
                    </tbody>
                </table>
            </div>
            <div id="course-pagination" class="px-6 py-4 bg-gray-50 flex justify-center">
                <!-- Pagination will be added here -->
            </div>
        `;
        courseContainer.appendChild(tableContainer);
        
        // Add event listeners
        document.getElementById('create-course-btn').addEventListener('click', this.showCreateCourseForm.bind(this));
        document.getElementById('course-search-btn').addEventListener('click', this.handleCourseSearch.bind(this));
        document.getElementById('course-search').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.handleCourseSearch();
            }
        });
        document.getElementById('course-filter-btn').addEventListener('click', this.handleCourseFilter.bind(this));
        
        // Load departments for filter
        this.loadDepartmentsForFilter();
        
        // Show loading indicator
        const tableBody = document.getElementById('courses-table-body');
        tableBody.innerHTML = `
            <tr>
                <td colspan="7" class="px-6 py-4 text-center text-gray-500">
                    <div class="flex justify-center items-center space-x-2">
                        <svg class="animate-spin h-5 w-5 text-primary" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                        </svg>
                        <span>Loading courses...</span>
                    </div>
                </td>
            </tr>
        `;
        
        // Load courses
        this.loadCourses();
    },

    /**
     * Load departments for filter dropdown
     */
    loadDepartmentsForFilter: async function() {
        try {
            const departments = await API.department.getAll();
            
            const departmentFilter = document.getElementById('department-filter');
            departmentFilter.innerHTML = '<option value="">All Departments</option>';
            
            departments.forEach(department => {
                const option = document.createElement('option');
                option.value = department.id;
                option.textContent = department.departmentName;
                departmentFilter.appendChild(option);
            });
        } catch (error) {
            console.error('Error loading departments for filter:', error);
        }
    },

    /**
     * Load courses with pagination
     */
    loadCourses: async function() {
        try {
            Utils.showLoading();
            
            console.log("Loading courses for page:", this.currentPage);
            const response = await API.course.getPaginated(this.currentPage, this.pageSize);
            
            console.log("Courses API response:", response);
            
            Utils.hideLoading();
            
            // Check if response has the expected structure
            if (!response || !response.success) {
                console.error("Invalid response format:", response);
                this.renderEmptyCourses("Error: Unexpected API response format");
                return;
            }
            
            // Handle the response data structure correctly
            const courses = response.data.content || [];
            this.totalPages = response.data.totalPages || 0;
            
            // Render courses
            this.renderCourses(courses);
            
            // Render pagination
            this.renderPagination();
        } catch (error) {
            Utils.hideLoading();
            console.error('Error loading courses:', error);
            this.renderEmptyCourses(`Failed to load courses: ${error.message || 'Unknown error'}`);
        }
    },

    /**
     * Render empty courses table with message
     * @param {string} message - Message to display
     */
    renderEmptyCourses: function(message) {
        const tableBody = document.getElementById('courses-table-body');
        tableBody.innerHTML = `
            <tr>
                <td colspan="7" class="px-6 py-4 text-center text-gray-500">
                    ${message || 'No courses found'}
                </td>
            </tr>
        `;
        
        // Clear pagination
        document.getElementById('course-pagination').innerHTML = '';
    },

    /**
     * Render courses in the table
     * @param {Array} courses - Array of course objects
     */
    renderCourses: function(courses) {
        console.log("Rendering courses:", courses);
        const tableBody = document.getElementById('courses-table-body');
        tableBody.innerHTML = '';
        
        if (!courses || courses.length === 0) {
            this.renderEmptyCourses();
            return;
        }
        
        courses.forEach(course => {
            const row = document.createElement('tr');
            row.className = 'hover:bg-gray-50';
            row.innerHTML = `
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${course.id || 'N/A'}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${course.courseCode || 'N/A'}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${course.courseName || 'N/A'}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${course.departmentName || 'N/A'}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${this.formatSemester(course.semester) || 'N/A'}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${course.creditHours || 'N/A'}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    <button class="text-primary hover:text-primary-dark mr-2 edit-course" data-id="${course.id}">Edit</button>
                    <button class="text-red-600 hover:text-red-800 delete-course" data-id="${course.id}">Delete</button>
                </td>
            `;
            tableBody.appendChild(row);
            
            // Add event listeners
            row.querySelector('.edit-course').addEventListener('click', () => this.handleEditCourse(course.id));
            row.querySelector('.delete-course').addEventListener('click', () => this.handleDeleteCourse(course.id));
        });
    },

    /**
     * Format semester for display
     * @param {string} semester - Semester value
     * @returns {string} Formatted semester
     */
    formatSemester: function(semester) {
        if (!semester) return 'N/A';
        
        // Convert SEMESTER_1 to "Semester 1"
        return semester.replace('SEMESTER_', 'Semester ');
    },

    /**
     * Show create course form
     */
    showCreateCourseForm: async function() {
        try {
            Utils.showLoading();
            
            // Load departments for dropdown
            const departments = await API.department.getAll();
            
            Utils.hideLoading();
            
            let departmentOptions = '<option value="">Select Department</option>';
            departments.forEach(department => {
                departmentOptions += `<option value="${department.id}">${department.departmentName}</option>`;
            });
            
            Swal.fire({
                title: 'Create New Course',
                html: `
                    <div class="mb-4">
                        <label for="course-code" class="block text-sm font-medium text-gray-700 mb-1">Course Code</label>
                        <input id="course-code" class="form-input w-full" placeholder="Enter course code">
                    </div>
                    <div class="mb-4">
                        <label for="course-name" class="block text-sm font-medium text-gray-700 mb-1">Course Name</label>
                        <input id="course-name" class="form-input w-full" placeholder="Enter course name">
                    </div>
                    <div class="mb-4">
                        <label for="department-id" class="block text-sm font-medium text-gray-700 mb-1">Department</label>
                        <select id="department-id" class="form-select w-full">
                            ${departmentOptions}
                        </select>
                    </div>
                    <div class="mb-4">
                        <label for="semester" class="block text-sm font-medium text-gray-700 mb-1">Semester</label>
                        <select id="semester" class="form-select w-full">
                            <option value="">Select Semester</option>
                            <option value="SEMESTER_1">Semester 1</option>
                            <option value="SEMESTER_2">Semester 2</option>
                            <option value="SEMESTER_3">Semester 3</option>
                            <option value="SEMESTER_4">Semester 4</option>
                            <option value="SEMESTER_5">Semester 5</option>
                            <option value="SEMESTER_6">Semester 6</option>
                            <option value="SEMESTER_7">Semester 7</option>
                            <option value="SEMESTER_8">Semester 8</option>
                        </select>
                    </div>
                    <div class="mb-4">
                        <label for="credit-hours" class="block text-sm font-medium text-gray-700 mb-1">Credit Hours</label>
                        <input id="credit-hours" type="number" min="1" max="6" class="form-input w-full" placeholder="Enter credit hours">
                    </div>
                    <div class="mb-4">
                        <label for="course-description" class="block text-sm font-medium text-gray-700 mb-1">Description</label>
                        <textarea id="course-description" class="form-textarea w-full" rows="3" placeholder="Enter course description"></textarea>
                    </div>
                `,
                showCancelButton: true,
                confirmButtonText: 'Create',
                confirmButtonColor: '#3B82F6',
                cancelButtonColor: '#6B7280',
                preConfirm: () => {
                    const courseCode = document.getElementById('course-code').value;
                    const courseName = document.getElementById('course-name').value;
                    const departmentId = document.getElementById('department-id').value;
                    const semester = document.getElementById('semester').value;
                    const creditHours = document.getElementById('credit-hours').value;
                    const courseDescription = document.getElementById('course-description').value;
                    
                    if (!courseCode) {
                        Swal.showValidationMessage('Course code is required');
                        return false;
                    }
                    
                    if (!courseName) {
                        Swal.showValidationMessage('Course name is required');
                        return false;
                    }
                    
                    if (!departmentId) {
                        Swal.showValidationMessage('Department is required');
                        return false;
                    }
                    
                    if (!semester) {
                        Swal.showValidationMessage('Semester is required');
                        return false;
                    }
                    
                    if (!creditHours) {
                        Swal.showValidationMessage('Credit hours is required');
                        return false;
                    }
                    
                    return {
                        courseCode,
                        courseName,
                        departmentId: parseInt(departmentId),
                        semester,
                        creditHours: parseInt(creditHours),
                        courseDescription
                    };
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    this.createCourse(result.value);
                }
            });
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to load departments');
        }
    },

    /**
     * Create new course
     * @param {Object} courseData - Course data
     */
    createCourse: async function(courseData) {
        try {
            Utils.showLoading();
            await API.course.create(courseData);
            Utils.hideLoading();
            Utils.showSuccess('Course created successfully');
            this.loadCourses();
        } catch (error) {
            Utils.hideLoading();
            Utils.showError(error.message || 'Failed to create course');
        }
    },

    /**
     * Handle edit course
     * @param {number} courseId - Course ID
     */
    handleEditCourse: async function(courseId) {
        try {
            Utils.showLoading();
            
            // Load course and departments
            const [course, departments] = await Promise.all([
                API.course.getById(courseId),
                API.department.getAll()
            ]);
            
            Utils.hideLoading();
            
            this.currentCourse = course;
            
            let departmentOptions = '<option value="">Select Department</option>';
            departments.forEach(department => {
                const selected = course.department && course.department.id === department.id ? 'selected' : '';
                departmentOptions += `<option value="${department.id}" ${selected}>${department.departmentName}</option>`;
            });
            
            Swal.fire({
                title: 'Edit Course',
                html: `
                    <div class="mb-4">
                        <label for="edit-course-code" class="block text-sm font-medium text-gray-700 mb-1">Course Code</label>
                        <input id="edit-course-code" class="form-input w-full" value="${course.courseCode}">
                    </div>
                    <div class="mb-4">
                        <label for="edit-course-name" class="block text-sm font-medium text-gray-700 mb-1">Course Name</label>
                        <input id="edit-course-name" class="form-input w-full" value="${course.courseName}">
                    </div>
                    <div class="mb-4">
                        <label for="edit-department-id" class="block text-sm font-medium text-gray-700 mb-1">Department</label>
                        <select id="edit-department-id" class="form-select w-full">
                            ${departmentOptions}
                        </select>
                    </div>
                    <div class="mb-4">
                        <label for="edit-semester" class="block text-sm font-medium text-gray-700 mb-1">Semester</label>
                        <select id="edit-semester" class="form-select w-full">
                            <option value="">Select Semester</option>
                            <option value="SEMESTER_1" ${course.semester === 'SEMESTER_1' ? 'selected' : ''}>Semester 1</option>
                            <option value="SEMESTER_2" ${course.semester === 'SEMESTER_2' ? 'selected' : ''}>Semester 2</option>
                            <option value="SEMESTER_3" ${course.semester === 'SEMESTER_3' ? 'selected' : ''}>Semester 3</option>
                            <option value="SEMESTER_4" ${course.semester === 'SEMESTER_4' ? 'selected' : ''}>Semester 4</option>
                            <option value="SEMESTER_5" ${course.semester === 'SEMESTER_5' ? 'selected' : ''}>Semester 5</option>
                            <option value="SEMESTER_6" ${course.semester === 'SEMESTER_6' ? 'selected' : ''}>Semester 6</option>
                            <option value="SEMESTER_7" ${course.semester === 'SEMESTER_7' ? 'selected' : ''}>Semester 7</option>
                            <option value="SEMESTER_8" ${course.semester === 'SEMESTER_8' ? 'selected' : ''}>Semester 8</option>
                        </select>
                    </div>
                    <div class="mb-4">
                        <label for="edit-credit-hours" class="block text-sm font-medium text-gray-700 mb-1">Credit Hours</label>
                        <input id="edit-credit-hours" type="number" min="1" max="6" class="form-input w-full" value="${course.creditHours}">
                    </div>
                    <div class="mb-4">
                        <label for="edit-course-description" class="block text-sm font-medium text-gray-700 mb-1">Description</label>
                        <textarea id="edit-course-description" class="form-textarea w-full" rows="3">${course.courseDescription || ''}</textarea>
                    </div>
                `,
                showCancelButton: true,
                confirmButtonText: 'Update',
                confirmButtonColor: '#3B82F6',
                cancelButtonColor: '#6B7280',
                preConfirm: () => {
                    const courseCode = document.getElementById('edit-course-code').value;
                    const courseName = document.getElementById('edit-course-name').value;
                    const departmentId = document.getElementById('edit-department-id').value;
                    const semester = document.getElementById('edit-semester').value;
                    const creditHours = document.getElementById('edit-credit-hours').value;
                    const courseDescription = document.getElementById('edit-course-description').value;
                    
                    if (!courseCode) {
                        Swal.showValidationMessage('Course code is required');
                        return false;
                    }
                    
                    if (!courseName) {
                        Swal.showValidationMessage('Course name is required');
                        return false;
                    }
                    
                    if (!departmentId) {
                        Swal.showValidationMessage('Department is required');
                        return false;
                    }
                    
                    if (!semester) {
                        Swal.showValidationMessage('Semester is required');
                        return false;
                    }
                    
                    if (!creditHours) {
                        Swal.showValidationMessage('Credit hours is required');
                        return false;
                    }
                    
                    return {
                        courseCode,
                        courseName,
                        departmentId: parseInt(departmentId),
                        semester,
                        creditHours: parseInt(creditHours),
                        courseDescription
                    };
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    this.updateCourse(courseId, result.value);
                }
            });
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to load course details');
        }
    },

    /**
     * Update course
     * @param {number} courseId - Course ID
     * @param {Object} courseData - Updated course data
     */
    updateCourse: async function(courseId, courseData) {
        try {
            Utils.showLoading();
            await API.course.update(courseId, courseData);
            Utils.hideLoading();
            Utils.showSuccess('Course updated successfully');
            this.loadCourses();
        } catch (error) {
            Utils.hideLoading();
            Utils.showError(error.message || 'Failed to update course');
        }
    },

    /**
     * Handle delete course
     * @param {number} courseId - Course ID
     */
    handleDeleteCourse: function(courseId) {
        Utils.showConfirm(
            'Delete Course',
            'Are you sure you want to delete this course? This action cannot be undone.',
            'Delete',
            async () => {
                try {
                    Utils.showLoading();
                    await API.course.delete(courseId);
                    Utils.hideLoading();
                    Utils.showSuccess('Course deleted successfully');
                    this.loadCourses();
                } catch (error) {
                    Utils.hideLoading();
                    Utils.showError(error.message || 'Failed to delete course');
                }
            }
        );
    },

    /**
     * Show courses by department
     * @param {number} departmentId - Department ID
     */
    showCoursesByDepartment: function(departmentId) {
        // Hide other sections
        document.getElementById('dashboard-container').classList.add('hidden');
        document.getElementById('college-container').classList.add('hidden');
        document.getElementById('faculty-container').classList.add('hidden');
        document.getElementById('department-container').classList.add('hidden');
        document.getElementById('program-container').classList.add('hidden');
        
        // Show course section
        const courseContainer = document.getElementById('course-container');
        courseContainer.classList.remove('hidden');
        
        // Set department filter
        this.currentDepartmentFilter = departmentId;
        this.currentSemesterFilter = '';
        this.currentSearchQuery = '';
        this.currentPage = 0;
        
        // Render course section
        this.renderCourseSection();
        
        // Set department filter dropdown value
        setTimeout(() => {
            const departmentFilter = document.getElementById('department-filter');
            if (departmentFilter) {
                departmentFilter.value = departmentId;
            }
        }, 100);
    },

    /**
     * Show courses by program
     * @param {number} programId - Program ID
     */
    showCoursesByProgram: async function(programId) {
        try {
            Utils.showLoading();
            
            // Get program with courses
            const program = await API.program.getWithCourses(programId);
            
            Utils.hideLoading();
            
            // Hide other sections
            document.getElementById('dashboard-container').classList.add('hidden');
            document.getElementById('college-container').classList.add('hidden');
            document.getElementById('faculty-container').classList.add('hidden');
            document.getElementById('department-container').classList.add('hidden');
            document.getElementById('program-container').classList.add('hidden');
            
            // Show course section
            const courseContainer = document.getElementById('course-container');
            courseContainer.classList.remove('hidden');
            
            // Render course section with program courses
            this.renderCourseSection();
            
            // Render program courses
            this.renderCourses(program.courses);
            
            // Hide pagination
            document.getElementById('course-pagination').innerHTML = '';
            
            // Add program info
            const programInfo = document.createElement('div');
            programInfo.className = 'bg-blue-50 p-4 rounded-lg mb-6';
            programInfo.innerHTML = `
                <h2 class="text-xl font-semibold mb-2">Courses for Program: ${program.programName}</h2>
                <p>Department: ${program.department ? program.department.departmentName : 'N/A'}</p>
            `;
            
            courseContainer.insertBefore(programInfo, courseContainer.querySelector('.bg-white.rounded-lg.shadow-md.overflow-hidden'));
        } catch (error) {
            Utils.hideLoading();
            Utils.showError('Failed to load program courses');
        }
    },

    /**
     * Get course count for dashboard
     * @returns {Promise<number>} Promise resolving to course count
     */
    getCourseCount: async function() {
        try {
            const response = await API.course.getAll();
            return response.data.length;
        } catch (error) {
            console.error('Error getting course count:', error);
            return 0;
        }
    },

    /**
     * Handle course search
     */
    handleCourseSearch: function() {
        const searchTerm = document.getElementById('course-search').value.trim();
        console.log("Searching for courses with term:", searchTerm);
        
        if (!searchTerm) {
            // If search term is empty, load all courses
            this.loadCourses();
            return;
        }
        
        // Filter courses by search term
        this.searchCourses(searchTerm);
    },

    /**
     * Search courses by term
     * @param {string} searchTerm - Search term
     */
    searchCourses: async function(searchTerm) {
        try {
            Utils.showLoading();
            
            // For now, we'll get all courses and filter client-side
            // In the future, this could be replaced with a server-side search
            const response = await API.course.getAll();
            
            Utils.hideLoading();
            
            if (!response || !response.success) {
                console.error("Invalid response format:", response);
                this.renderEmptyCourses("Error: Unexpected API response format");
                return;
            }
            
            const courses = response.data || [];
            
            // Filter courses by search term (case-insensitive)
            const filteredCourses = courses.filter(course => 
                (course.courseCode && course.courseCode.toLowerCase().includes(searchTerm.toLowerCase())) ||
                (course.courseName && course.courseName.toLowerCase().includes(searchTerm.toLowerCase())) ||
                (course.departmentName && course.departmentName.toLowerCase().includes(searchTerm.toLowerCase()))
            );
            
            // Render filtered courses
            this.renderCourses(filteredCourses);
            
            // Clear pagination since we're showing filtered results
            document.getElementById('course-pagination').innerHTML = '';
            
            // Show message if no results
            if (filteredCourses.length === 0) {
                this.renderEmptyCourses(`No courses found matching "${searchTerm}"`);
            }
        } catch (error) {
            Utils.hideLoading();
            console.error('Error searching courses:', error);
            this.renderEmptyCourses(`Failed to search courses: ${error.message || 'Unknown error'}`);
        }
    },

    /**
     * Handle course filter
     */
    handleCourseFilter: function() {
        const departmentId = document.getElementById('department-filter').value;
        const semester = document.getElementById('semester-filter').value;

        console.log("Filtering courses by department:", departmentId, "and semester:", semester);

        if (!departmentId && !semester) {
            // If no filters are applied, load all courses
            this.loadCourses();
            return;
        }

        // Apply filters
        this.filterCourses(departmentId, semester);
    },

    /**
     * Filter courses by department and semester
     * @param {string} departmentId - Department ID
     * @param {string} semester - Semester
     */
    filterCourses: async function(departmentId, semester) {
        try {
            Utils.showLoading();

            let response;

            if (departmentId && semester) {
                response = await API.course.getFiltered(departmentId, semester);
            } else if (departmentId) {
                response = await API.course.getByDepartment(departmentId);
            } else if (semester) {
                response = await API.course.getBySemester(semester);
            } else {
                response = await API.course.getAll();
            }

            console.log("Filtered courses response:", response);

            Utils.hideLoading();

            if (!response || !response.success) {
                console.error("Invalid response format:", response);
                this.renderEmptyCourses("Error: Unexpected API response format");
                return;
            }

            const courses = response.data || [];
            this.totalPages = response.totalPages || 0;

            this.renderCourses(courses);
            this.renderPagination();
        } catch (error) {
            Utils.hideLoading();
            console.error('Error filtering courses:', error);
            this.renderEmptyCourses(`Failed to filter courses: ${error.message || 'Unknown error'}`);
        }
    },

    /**
     * Render pagination controls
     */
    renderPagination: function() {
        const paginationContainer = document.getElementById('course-pagination');
        paginationContainer.innerHTML = '';
        
        if (this.totalPages <= 1) {
            return;
        }
        
        const pagination = Utils.createPagination(this.currentPage, this.totalPages, (page) => {
            this.currentPage = page;
            this.loadCourses();
        });
        
        paginationContainer.appendChild(pagination);
    },
};

/**
 * Course management module for the Academic Management System
 */

const Course = {
    // Current page for pagination
    currentPage: 0,
    
    // Page size for pagination
    pageSize: 10,
    
    // Total number of pages
    totalPages: 0,
    
    // Current course being edited
    currentCourse: null,

    /**
     * Initialize course module
     */
    init: function() {
        console.log("Course module initialized");
        this.bindEvents();
    },

    /**
     * Bind event listeners
     */
    bindEvents: function() {
        // Navigation link
        const courseLinks = document.querySelectorAll('[data-page="course"]');
        courseLinks.forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                console.log("Course link clicked");
                this.showCourseSection();
            });
        });
    },

    /**
     * Show course section
     */
    showCourseSection: function() {
        console.log("Showing course section");
        // Hide other sections
        document.getElementById('dashboard-container').classList.add('hidden');
        document.getElementById('college-container').classList.add('hidden');
        document.getElementById('faculty-container').classList.add('hidden');
        document.getElementById('department-container').classList.add('hidden');
        document.getElementById('program-container').classList.add('hidden');
        
        // Show course section
        const courseContainer = document.getElementById('course-container');
        courseContainer.classList.remove('hidden');
        
        // Render course section
        this.renderCourseSection();
    },

    /**
     * Render course section
     */
    renderCourseSection: function() {
        console.log("Rendering course section");
        const courseContainer = document.getElementById('course-container');
        courseContainer.innerHTML = '';
        
        // Create section header
        const header = document.createElement('div');
        header.className = 'flex justify-between items-center mb-6';
        header.innerHTML = `
            <h1 class="text-3xl font-bold text-gray-800">Courses</h1>
            <div class="flex space-x-2">
                <button id="create-course-btn" class="bg-primary hover:bg-blue-700 text-white px-4 py-2 rounded">
                    <i class="fas fa-plus mr-2"></i> Add Course
                </button>
            </div>
        `;
        courseContainer.appendChild(header);
        
        // Create search and filter section
        const filterSection = document.createElement('div');
        filterSection.className = 'bg-white rounded-lg shadow-md p-4 mb-6';
        filterSection.innerHTML = `
            <div class="flex flex-col md:flex-row md:items-center md:justify-between space-y-4 md:space-y-0">
                <div class="flex flex-col md:flex-row md:items-center space-y-4 md:space-y-0 md:space-x-4">
                    <div>
                        <label for="department-filter" class="block text-sm font-medium text-gray-700 mb-1">Department</label>
                        <select id="department-filter" class="rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                            <option value="">All Departments</option>
                            <!-- Departments will be loaded dynamically -->
                        </select>
                    </div>
                    <div>
                        <label for="semester-filter" class="block text-sm font-medium text-gray-700 mb-1">Semester</label>
                        <select id="semester-filter" class="rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                            <option value="">All Semesters</option>
                            <option value="SEMESTER_1">Semester 1</option>
                            <option value="SEMESTER_2">Semester 2</option>
                            <option value="SEMESTER_3">Semester 3</option>
                            <option value="SEMESTER_4">Semester 4</option>
                            <option value="SEMESTER_5">Semester 5</option>
                            <option value="SEMESTER_6">Semester 6</option>
                            <option value="SEMESTER_7">Semester 7</option>
                            <option value="SEMESTER_8">Semester 8</option>
                        </select>
                    </div>
                    <div class="md:self-end">
                        <button id="course-filter-btn" class="bg-gray-200 hover:bg-gray-300 text-gray-800 px-4 py-2 rounded">
                            Apply Filters
                        </button>
                    </div>
                </div>
                <div class="flex items-center space-x-2">
                    <div>
                        <input id="course-search" type="text" placeholder="Search courses..." class="rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                    </div>
                    <button id="course-search-btn" class="bg-primary hover:bg-blue-700 text-white px-4 py-2 rounded">
                        <i class="fas fa-search mr-2"></i> Search
                    </button>
                </div>
            </div>
        `;
        courseContainer.appendChild(filterSection);
        
        // Create courses table
        const tableContainer = document.createElement('div');
        tableContainer.className = 'bg-white rounded-lg shadow-md overflow-hidden';
        tableContainer.innerHTML = `
            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Course Code</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Course Name</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Department</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Semester</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Credit Hours</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                        </tr>
                    </thead>
                    <tbody id="courses-table-body" class="bg-white divide-y divide-gray-200">
                        <!-- Courses will be loaded here -->
                    </tbody>
                </table>
            </div>
            <div id="course-pagination" class="px-6 py-4 bg-gray-50 flex justify-center">
                <!-- Pagination will be added here -->
            </div>
        `;
        courseContainer.appendChild(tableContainer);
        
        // Add event listeners
        document.getElementById('create-course-btn').addEventListener('click', this.showCreateCourseForm.bind(this));
        document.getElementById('course-search-btn').addEventListener('click', this.handleCourseSearch.bind(this));
        document.getElementById('course-search').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.handleCourseSearch();
            }
        });
        document.getElementById('course-filter-btn').addEventListener('click', this.handleCourseFilter.bind(this));
        
        // Load departments for filter
        this.loadDepartmentsForFilter();
        
        // Show loading indicator
        const tableBody = document.getElementById('courses-table-body');
        tableBody.innerHTML = `
            <tr>
                <td colspan="7" class="px-6 py-4 text-center text-gray-500">
                    <div class="flex justify-center items-center space-x-2">
                        <svg class="animate-spin h-5 w-5 text-primary" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                        </svg>
                        <span>Loading courses...</span>
                    </div>
                </td>
            </tr>
        `;
        
        // Load courses
        this.loadCourses();
    },

    /**
     * Load departments for filter dropdown
     */
    loadDepartmentsForFilter: async function() {
        try {
            const departments = await API.department.getAll();
            
            const departmentFilter = document.getElementById('department-filter');
            departmentFilter.innerHTML = '<option value="">All Departments</option>';
            
            departments.forEach(department => {
                const option = document.createElement('option');
                option.value = department.id;
                option.textContent = department.departmentName;
               
                            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                        </svg>
                        <span>Loading courses...</span>
                    </div>
                </td>
            </tr>
        `;
        
        // Load courses
        this.loadCourses();
    },

    /**
     * Load departments for filter dropdown
     */
    loadDepartmentsForFilter: async function() {
        try {
            const departments = await API.department.getAll();
            
            const departmentFilter = document.getElementById('department-filter');
            departmentFilter.innerHTML = '<option value="">All Departments</option>';
            
            departments.forEach(department => {
                const option = document.createElement('option');
                option.value = department.id;
                option.textContent = department.departmentName;
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Course Code</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Course Name</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Department</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Semester</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Credit Hours</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                        </tr>
                    </thead>
                    <tbody id="courses-table-body" class="bg-white divide-y divide-gray-200">
                        <!-- Courses will be loaded here -->
                    </tbody>
                </table>
            </div>
            <div id="course-pagination" class="px-6 py-4 bg-gray-50 flex justify-center">
                <!-- Pagination will be added here -->
            </div>
        `;
        courseContainer.appendChild(tableContainer);
        
        // Add event listeners
                    <div class="md:self-end">
                        <button id="course-filter-btn" class="bg-gray-200 hover:bg-gray-300 text-gray-800 px-4 py-2 rounded">
                            Apply Filters
                        </button>
                    </div>
                </div>
                <div class="flex items-center space-x-2">
                    <div>
                        <input id="course-search" type="text" placeholder="Search courses..." class="rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                    </div>
                    <button id="course-search-btn" class="bg-primary hover:bg-blue-700 text-white px-4 py-2 rounded">
                        <i class="fas fa-search mr-2"></i> Search
                    </button>
                </div>
            </div>
        `;
        courseContainer.appendChild(filterSection);
        
        // Create courses table
        const tableContainer = document.createElement('div');
        tableContainer.className = 'bg-white rounded-lg shadow-md overflow-hidden';
        tableContainer.innerHTML = `
            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Course Code</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Course Name</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Department</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Semester</th>
            if (departmentId && semester) {
