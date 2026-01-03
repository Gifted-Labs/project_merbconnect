/**
 * API Service for the Academic Management System
 */

const API = {
    // Base URL for API requests
    baseUrl: '/api/v1',

    /**
     * Get authentication token from local storage
     * @returns {string|null} Authentication token or null if not found
     */
    getToken: function() {
        return localStorage.getItem('token');
    },

    /**
     * Set authentication token in local storage
     * @param {string} token - Authentication token
     */
    setToken: function(token) {
        localStorage.setItem('token', token);
    },

    /**
     * Remove authentication token from local storage
     */
    removeToken: function() {
        localStorage.removeItem('token');
    },

    /**
     * Check if user is authenticated
     * @returns {boolean} True if user is authenticated
     */
    isAuthenticated: function() {
        return !!this.getToken();
    },

    /**
     * Get default headers for API requests
     * @param {boolean} includeAuth - Whether to include authentication header
     * @returns {Object} Headers object
     */
    getHeaders: function(includeAuth = true) {
        const headers = {
            'Content-Type': 'application/json'
        };

        if (includeAuth && this.isAuthenticated()) {
            headers['Authorization'] = `Bearer ${this.getToken()}`;
        }

        return headers;
    },

    /**
     * Make API request
     * @param {string} endpoint - API endpoint
     * @param {string} method - HTTP method
     * @param {Object} data - Request data
     * @param {boolean} includeAuth - Whether to include authentication header
     * @returns {Promise} Promise resolving to response data
     */
    request: async function(endpoint, method = 'GET', data = null, includeAuth = true) {
        const url = `${this.baseUrl}${endpoint}`;
        const options = {
            method,
            headers: this.getHeaders(includeAuth)
        };

        if (data && (method === 'POST' || method === 'PUT' || method === 'PATCH')) {
            options.body = JSON.stringify(data);
        }

        console.log(`API Request: ${method} ${url}`, options);

        try {
            const response = await fetch(url, options);
            console.log(`API Response status: ${response.status} ${response.statusText}`);

            let responseData;
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                responseData = await response.json();
                console.log('API Response data:', responseData);
            } else {
                const text = await response.text();
                console.log('API Response text:', text);
                try {
                    responseData = JSON.parse(text);
                } catch (e) {
                    responseData = { message: text };
                }
            }

            if (!response.ok) {
                const errorMessage = responseData.message || responseData.error || 'An error occurred';
                console.error(`API Error (${response.status}): ${errorMessage}`);
                throw new Error(errorMessage);
            }

            // Normalize response format
            if (responseData && !responseData.data && !responseData.success) {
                // If response is just an array or object without data property, wrap it
                if (Array.isArray(responseData)) {
                    return { success: true, data: responseData };
                } else if (typeof responseData === 'object') {
                    // Check if it's a paginated response
                    if (responseData.content && responseData.pageable) {
                        return { success: true, data: responseData };
                    }
                    // Otherwise, check if it's already a proper response
                    if (!responseData.data) {
                        return { success: true, data: responseData };
                    }
                }
            }

            return responseData;
        } catch (error) {
            console.error(`API Request Error: ${error.message}`);
            throw error;
        }
    },

    /**
     * Authentication API
     */
    auth: {
        /**
         * Login user
         * @param {string} email - User email
         * @param {string} password - User password
         * @returns {Promise} Promise resolving to user data
         */
        login: async function(email, password) {
            const response = await API.request('/auth/login', 'POST', { email, password }, false);
            API.setToken(response.token);
            return response;
        },

        /**
         * Register user
         * @param {Object} userData - User registration data
         * @returns {Promise} Promise resolving to registration response
         */
        register: async function(userData) {
            return await API.request('/auth/signup', 'POST', userData, false);
        },

        /**
         * Logout user
         */
        logout: function() {
            API.removeToken();
        }
    },

    /**
     * College API
     */
    college: {
        /**
         * Get all colleges
         * @returns {Promise} Promise resolving to colleges data
         */
        getAll: async function() {
            try {
                const response = await API.request('/academics/colleges');
                console.log('College API response:', response);
                return response;
            } catch (error) {
                console.error('Error in college.getAll:', error);
                return { data: [] }; // Return empty array on error
            }
        },

        /**
         * Get colleges with pagination
         * @param {number} page - Page number
         * @param {number} size - Page size
         * @returns {Promise} Promise resolving to paginated colleges data
         */
        getPaginated: async function(page = 0, size = 10) {
            const response = await API.request(`/colleges/paged?page=${page}&size=${size}`);
            return response.data;
        },

        /**
         * Get college by ID
         * @param {number} id - College ID
         * @returns {Promise} Promise resolving to college data
         */
        getById: async function(id) {
            const response = await API.request(`/colleges/${id}`);
            return response.data;
        },

        /**
         * Create new college
         * @param {Object} collegeData - College data
         * @returns {Promise} Promise resolving to created college data
         */
        create: async function(collegeData) {
            const response = await API.request('/colleges', 'POST', collegeData);
            return response.data;
        },

        /**
         * Update college
         * @param {number} id - College ID
         * @param {Object} collegeData - Updated college data
         * @returns {Promise} Promise resolving to updated college data
         */
        update: async function(id, collegeData) {
            const response = await API.request(`/colleges/${id}`, 'PUT', collegeData);
            return response.data;
        },

        /**
         * Delete college
         * @param {number} id - College ID
         * @returns {Promise} Promise resolving to deletion response
         */
        delete: async function(id) {
            return await API.request(`/colleges/${id}`, 'DELETE');
        },

        /**
         * Search colleges by name
         * @param {string} name - College name to search for
         * @returns {Promise} Promise resolving to search results
         */
        search: async function(name) {
            const response = await API.request(`/colleges/search?name=${encodeURIComponent(name)}`);
            return response.data;
        },

        /**
         * Get college with faculties
         * @param {number} id - College ID
         * @returns {Promise} Promise resolving to college with faculties data
         */
        getWithFaculties: async function(id) {
            const response = await API.request(`/colleges/${id}/with-faculties`);
            return response.data;
        }
    },

    /**
     * Faculty API
     */
    faculty: {
        /**
         * Get all faculties
         * @returns {Promise} Promise resolving to faculties data
         */
        getAll: async function() {
            try {
                const response = await API.request('/academics/faculties');
                console.log('Faculty API response:', response);
                return response;
            } catch (error) {
                console.error('Error in faculty.getAll:', error);
                return { data: [] }; // Return empty array on error
            }
        },

        /**
         * Get faculties with pagination
         * @param {number} page - Page number
         * @param {number} size - Page size
         * @returns {Promise} Promise resolving to paginated faculties data
         */
        getPaginated: async function(page = 0, size = 10) {
            const response = await API.request(`/faculties/paged?page=${page}&size=${size}`);
            return response.data;
        },

        /**
         * Get faculty by ID
         * @param {number} id - Faculty ID
         * @returns {Promise} Promise resolving to faculty data
         */
        getById: async function(id) {
            const response = await API.request(`/faculties/${id}`);
            return response.data;
        },

        /**
         * Create new faculty
         * @param {Object} facultyData - Faculty data
         * @returns {Promise} Promise resolving to created faculty data
         */
        create: async function(facultyData) {
            const response = await API.request('/faculties', 'POST', facultyData);
            return response.data;
        },

        /**
         * Update faculty
         * @param {number} id - Faculty ID
         * @param {Object} facultyData - Updated faculty data
         * @returns {Promise} Promise resolving to updated faculty data
         */
        update: async function(id, facultyData) {
            const response = await API.request(`/faculties/${id}`, 'PUT', facultyData);
            return response.data;
        },

        /**
         * Delete faculty
         * @param {number} id - Faculty ID
         * @returns {Promise} Promise resolving to deletion response
         */
        delete: async function(id) {
            return await API.request(`/faculties/${id}`, 'DELETE');
        },

        /**
         * Get faculties by college ID
         * @param {number} collegeId - College ID
         * @returns {Promise} Promise resolving to faculties data
         */
        getByCollegeId: async function(collegeId) {
            const response = await API.request(`/faculties/college/${collegeId}`);
            return response.data;
        }
    },

    /**
     * Department API
     */
    department: {
        /**
         * Get all departments
         * @returns {Promise} Promise resolving to departments data
         */
        getAll: async function() {
            try {
                const response = await API.request('/academics/departments');
                console.log('Department API response:', response);
                return response;
            } catch (error) {
                console.error('Error in department.getAll:', error);
                return { data: [] }; // Return empty array on error
            }
        },

        /**
         * Get departments with pagination
         * @param {number} page - Page number
         * @param {number} size - Page size
         * @returns {Promise} Promise resolving to paginated departments data
         */
        getPaginated: async function(page = 0, size = 10) {
            try {
                const response = await API.request(`/departments/paged?page=${page}&size=${size}`);
                return response.data;
            } catch (error) {
                console.error('Error in department.getPaginated:', error);
                throw error;
            }
        },

        /**
         * Get department by ID
         * @param {number} id - Department ID
         * @returns {Promise} Promise resolving to department data
         */
        getById: async function(id) {
            const response = await API.request(`/departments/${id}`);
            return response.data;
        },

        /**
         * Create new department
         * @param {Object} departmentData - Department data
         * @returns {Promise} Promise resolving to created department data
         */
        create: async function(departmentData) {
            const response = await API.request('/departments', 'POST', departmentData);
            return response.data;
        },

        /**
         * Update department
         * @param {number} id - Department ID
         * @param {Object} departmentData - Updated department data
         * @returns {Promise} Promise resolving to updated department data
         */
        update: async function(id, departmentData) {
            const response = await API.request(`/departments/${id}`, 'PUT', departmentData);
            return response.data;
        },

        /**
         * Delete department
         * @param {number} id - Department ID
         * @returns {Promise} Promise resolving to deletion response
         */
        delete: async function(id) {
            return await API.request(`/departments/${id}`, 'DELETE');
        },

        /**
         * Get departments by faculty ID
         * @param {number} facultyId - Faculty ID
         * @returns {Promise} Promise resolving to departments data
         */
        getByFacultyId: async function(facultyId) {
            const response = await API.request(`/departments/faculty/${facultyId}`);
            return response.data;
        },

        /**
         * Get departments by college ID
         * @param {number} collegeId - College ID
         * @returns {Promise} Promise resolving to departments data
         */
        getByCollegeId: async function(collegeId) {
            const response = await API.request(`/departments/college/${collegeId}`);
            return response.data;
        },

        /**
         * Search departments by name
         * @param {string} name - Department name to search for
         * @returns {Promise} Promise resolving to search results
         */
        search: async function(name) {
            const response = await API.request(`/departments/search?name=${encodeURIComponent(name)}`);
            return response.data;
        },

        /**
         * Get department with programs
         * @param {number} id - Department ID
         * @returns {Promise} Promise resolving to department with programs data
         */
        getWithPrograms: async function(id) {
            const response = await API.request(`/departments/${id}/with-programs`);
            return response.data;
        }
    },

    /**
     * Program API
     */
    program: {
        /**
         * Get all programs
         * @returns {Promise} Promise resolving to programs data
         */
        getAll: async function() {
            try {
                const response = await API.request('/academics/programs');
                console.log('Program API response:', response);
                return response;
            } catch (error) {
                console.error('Error in program.getAll:', error);
                return { data: [] }; // Return empty array on error
            }
        },

        /**
         * Get programs with pagination
         * @param {number} page - Page number
         * @param {number} size - Page size
         * @returns {Promise} Promise resolving to paginated programs data
         */
        getPaginated: async function(page = 0, size = 10) {
            const response = await API.request(`/programs/paged?page=${page}&size=${size}`);
            return response.data;
        },

        /**
         * Get program by ID
         * @param {number} id - Program ID
         * @returns {Promise} Promise resolving to program data
         */
        getById: async function(id) {
            const response = await API.request(`/programs/${id}`);
            return response.data;
        },

        /**
         * Create new program
         * @param {Object} programData - Program data
         * @returns {Promise} Promise resolving to created program data
         */
        create: async function(programData) {
            const response = await API.request('/programs', 'POST', programData);
            return response.data;
        },

        /**
         * Update program
         * @param {number} id - Program ID
         * @param {Object} programData - Updated program data
         * @returns {Promise} Promise resolving to updated program data
         */
        update: async function(id, programData) {
            const response = await API.request(`/programs/${id}`, 'PUT', programData);
            return response.data;
        },

        /**
         * Delete program
         * @param {number} id - Program ID
         * @returns {Promise} Promise resolving to deletion response
         */
        delete: async function(id) {
            return await API.request(`/programs/${id}`, 'DELETE');
        },

        /**
         * Get programs by department ID
         * @param {number} departmentId - Department ID
         * @returns {Promise} Promise resolving to programs data
         */
        getByDepartmentId: async function(departmentId) {
            const response = await API.request(`/programs/department/${departmentId}`);
            return response.data;
        },

        /**
         * Get programs by faculty ID
         * @param {number} facultyId - Faculty ID
         * @returns {Promise} Promise resolving to programs data
         */
        getByFacultyId: async function(facultyId) {
            const response = await API.request(`/programs/faculty/${facultyId}`);
            return response.data;
        },

        /**
         * Get programs by college ID
         * @param {number} collegeId - College ID
         * @returns {Promise} Promise resolving to programs data
         */
        getByCollegeId: async function(collegeId) {
            const response = await API.request(`/programs/college/${collegeId}`);
            return response.data;
        },

        /**
         * Search programs by name
         * @param {string} name - Program name to search for
         * @returns {Promise} Promise resolving to search results
         */
        search: async function(name) {
            const response = await API.request(`/programs/search?name=${encodeURIComponent(name)}`);
            return response.data;
        },

        /**
         * Add course to program
         * @param {number} programId - Program ID
         * @param {number} courseId - Course ID
         * @returns {Promise} Promise resolving to updated program data
         */
        addCourse: async function(programId, courseId) {
            const response = await API.request(`/programs/${programId}/courses/${courseId}`, 'POST');
            return response.data;
        },

        /**
         * Remove course from program
         * @param {number} programId - Program ID
         * @param {number} courseId - Course ID
         * @returns {Promise} Promise resolving to updated program data
         */
        removeCourse: async function(programId, courseId) {
            const response = await API.request(`/programs/${programId}/courses/${courseId}`, 'DELETE');
            return response.data;
        }
    },

    /**
     * Course API
     */
    course: {
        /**
         * Get all courses
         * @returns {Promise} Promise resolving to courses data
         */
        getAll: async function() {
            try {
                const response = await API.request('/courses');
                console.log('Course API response:', response);
                return response;
            } catch (error) {
                console.error('Error in course.getAll:', error);
                return { data: [] }; // Return empty array on error
            }
        },
        
        /**
         * Get courses with pagination
         * @param {number} page - Page number
         * @param {number} size - Page size
         * @returns {Promise} Promise resolving to paginated courses data
         */
        getPaginated: async function(page = 0, size = 10) {
            try {
                console.log(`API: Getting paginated courses (page ${page}, size ${size})`);
                const response = await API.request(`/courses/paged?page=${page}&size=${size}`);
                console.log("API: Got courses response", response);
                return response;
            } catch (error) {
                console.error("API: Error getting paginated courses", error);
                throw error;
            }
        },
        
        /**
         * Get course by ID
         * @param {number} id - Course ID
         * @returns {Promise} Promise resolving to course data
         */
        getById: async function(id) {
            const response = await API.request(`/courses/${id}`);
            return response.data;
        },
        
        /**
         * Get course by course code
         * @param {string} courseCode - Course code
         * @returns {Promise} Promise resolving to course data
         */
        getByCourseCode: async function(courseCode) {
            const response = await API.request(`/courses/code/${courseCode}`);
            return response.data;
        },
        
        /**
         * Create new course
         * @param {Object} courseData - Course data
         * @returns {Promise} Promise resolving to created course data
         */
        create: async function(courseData) {
            const response = await API.request('/courses', 'POST', courseData);
            return response.data;
        },
        
        /**
         * Update course
         * @param {number} id - Course ID
         * @param {Object} courseData - Updated course data
         * @returns {Promise} Promise resolving to updated course data
         */
        update: async function(id, courseData) {
            const response = await API.request(`/courses/${id}`, 'PUT', courseData);
            return response.data;
        },
        
        /**
         * Delete course
         * @param {number} id - Course ID
         * @returns {Promise} Promise resolving to deletion response
         */
        delete: async function(id) {
            return await API.request(`/courses/${id}`, 'DELETE');
        },
        
        /**
         * Get courses by department ID
         * @param {number} departmentId - Department ID
         * @returns {Promise} Promise resolving to courses data
         */
        getByDepartmentId: async function(departmentId) {
            try {
                console.log(`API: Getting courses by department ID ${departmentId}`);
                const response = await API.request(`/courses/department/${departmentId}`);
                console.log("API: Got courses by department response", response);
                return response;
            } catch (error) {
                console.error(`API: Error getting courses by department ${departmentId}`, error);
                throw error;
            }
        },
        
        /**
         * Get courses by faculty ID
         * @param {number} facultyId - Faculty ID
         * @returns {Promise} Promise resolving to courses data
         */
        getByFacultyId: async function(facultyId) {
            const response = await API.request(`/courses/faculty/${facultyId}`);
            return response.data;
        },
        
        /**
         * Get courses by college ID
         * @param {number} collegeId - College ID
         * @returns {Promise} Promise resolving to courses data
         */
        getByCollegeId: async function(collegeId) {
            const response = await API.request(`/courses/college/${collegeId}`);
            return response.data;
        },
        
        /**
         * Get courses by program ID
         * @param {number} programId - Program ID
         * @returns {Promise} Promise resolving to courses data
         */
        getByProgramId: async function(programId) {
            const response = await API.request(`/courses/program/${programId}`);
            return response.data;
        },
        
        /**
         * Get courses by semester
         * @param {string} semester - Semester (SEMESTER_1, SEMESTER_2, etc.)
         * @returns {Promise} Promise resolving to courses data
         */
        getBySemester: async function(semester) {
            try {
                console.log(`API: Getting courses by semester ${semester}`);
                const response = await API.request(`/courses/semester/${semester}`);
                console.log("API: Got courses by semester response", response);
                return response;
            } catch (error) {
                console.error(`API: Error getting courses by semester ${semester}`, error);
                throw error;
            }
        },
        
        /**
         * Get courses by department ID and semester
         * @param {number} departmentId - Department ID
         * @param {string} semester - Semester (SEMESTER_1, SEMESTER_2, etc.)
         * @returns {Promise} Promise resolving to courses data
         */
        getByDepartmentIdAndSemester: async function(departmentId, semester) {
            try {
                console.log(`API: Getting courses by department ID ${departmentId} and semester ${semester}`);
                const response = await API.request(`/courses/department/${departmentId}/semester/${semester}`);
                console.log("API: Got courses by department and semester response", response);
                return response;
            } catch (error) {
                console.error(`API: Error getting courses by department ${departmentId} and semester ${semester}`, error);
                throw error;
            }
        },
        
        /**
         * Search courses by name or code
         * @param {string} query - Search query
         * @returns {Promise} Promise resolving to search results
         */
        search: async function(query) {
            const response = await API.request(`/courses/search?query=${encodeURIComponent(query)}`);
            return response.data;
        },
        
        /**
         * Get filtered courses with pagination
         * @param {Object} filters - Filter criteria
         * @param {number} page - Page number
         * @param {number} size - Page size
         * @returns {Promise} Promise resolving to filtered courses data
         */
        getFiltered: async function(filters, page = 0, size = 10) {
            let url = `/courses/filter?page=${page}&size=${size}`;
            
            if (filters.departmentId) {
                url += `&departmentId=${filters.departmentId}`;
            }
            
            if (filters.semester) {
                url += `&semester=${filters.semester}`;
            }
            
            if (filters.query) {
                url += `&query=${encodeURIComponent(filters.query)}`;
            }
            
            const response = await API.request(url);
            return response;
        }
    },

    /**
     * Quiz API
     */
    quiz: {
        /**
         * Get all quizzes
         * @returns {Promise} Promise resolving to quizzes data
         */
        getAll: async function() {
            const response = await API.request('/quizzes');
            return response;
        },
        
        /**
         * Get quizzes with pagination
         * @param {number} page - Page number
         * @param {number} size - Page size
         * @returns {Promise} Promise resolving to paginated quizzes data
         */
        getPaginated: async function(page = 0, size = 10) {
            try {
                console.log(`API: Getting paginated quizzes (page ${page}, size ${size})`);
                const response = await API.request(`/quizzes/paged?page=${page}&size=${size}`);
                console.log("API: Got quizzes response", response);
                return response;
            } catch (error) {
                console.error("API: Error getting paginated quizzes", error);
                throw error;
            }
        },
        
        /**
         * Get quiz by ID
         * @param {number} id - Quiz ID
         * @returns {Promise} Promise resolving to quiz data
         */
        getById: async function(id) {
            const response = await API.request(`/quizzes/${id}`);
            return response;
        },
        
        /**
         * Create new quiz
         * @param {Object} quizData - Quiz data
         * @returns {Promise} Promise resolving to created quiz data
         */
        create: async function(quizData) {
            const response = await API.request('/quizzes', 'POST', quizData);
            return response;
        },
        
        /**
         * Update quiz
         * @param {number} id - Quiz ID
         * @param {Object} quizData - Updated quiz data
         * @returns {Promise} Promise resolving to updated quiz data
         */
        update: async function(id, quizData) {
            const response = await API.request(`/quizzes/${id}`, 'PUT', quizData);
            return response;
        },
        
        /**
         * Delete quiz
         * @param {number} id - Quiz ID
         * @returns {Promise} Promise resolving to deletion response
         */
        delete: async function(id) {
            return await API.request(`/quizzes/${id}`, 'DELETE');
        },
        
        /**
         * Get quizzes by course ID
         * @param {number} courseId - Course ID
         * @param {number} page - Page number
         * @param {number} size - Page size
         * @returns {Promise} Promise resolving to quizzes data
         */
        getByCourse: async function(courseId, page = 0, size = 10) {
            const response = await API.request(`/quizzes/course/${courseId}?page=${page}&size=${size}`);
            return response;
        },
        
        /**
         * Get quizzes by quiz type
         * @param {string} quizType - Quiz type
         * @param {number} page - Page number
         * @param {number} size - Page size
         * @returns {Promise} Promise resolving to quizzes data
         */
        getByType: async function(quizType, page = 0, size = 10) {
            const response = await API.request(`/quizzes/type/${quizType}?page=${page}&size=${size}`);
            return response;
        },
        
        /**
         * Get quizzes by year
         * @param {number} year - Year
         * @param {number} page - Page number
         * @param {number} size - Page size
         * @returns {Promise} Promise resolving to quizzes data
         */
        getByYear: async function(year, page = 0, size = 10) {
            const response = await API.request(`/quizzes/year/${year}?page=${page}&size=${size}`);
            return response;
        },
        
        /**
         * Get quizzes by course ID and quiz type
         * @param {number} courseId - Course ID
         * @param {string} quizType - Quiz type
         * @param {number} page - Page number
         * @param {number} size - Page size
         * @returns {Promise} Promise resolving to quizzes data
         */
        getByCourseAndType: async function(courseId, quizType, page = 0, size = 10) {
            const response = await API.request(`/quizzes/course/${courseId}/type/${quizType}?page=${page}&size=${size}`);
            return response;
        },
        
        /**
         * Get quizzes by course ID and year
         * @param {number} courseId - Course ID
         * @param {number} year - Year
         * @param {number} page - Page number
         * @param {number} size - Page size
         * @returns {Promise} Promise resolving to quizzes data
         */
        getByCourseAndYear: async function(courseId, year, page = 0, size = 10) {
            const response = await API.request(`/quizzes/course/${courseId}/year/${year}?page=${page}&size=${size}`);
            return response;
        },
        
        /**
         * Get quizzes by quiz type and year
         * @param {string} quizType - Quiz type
         * @param {number} year - Year
         * @param {number} page - Page number
         * @param {number} size - Page size
         * @returns {Promise} Promise resolving to quizzes data
         */
        getByTypeAndYear: async function(quizType, year, page = 0, size = 10) {
            const response = await API.request(`/quizzes/type/${quizType}/year/${year}?page=${page}&size=${size}`);
            return response;
        },
        
        /**
         * Get quizzes by course ID, quiz type, and year
         * @param {number} courseId - Course ID
         * @param {string} quizType - Quiz type
         * @param {number} year - Year
         * @param {number} page - Page number
         * @param {number} size - Page size
         * @returns {Promise} Promise resolving to quizzes data
         */
        getByCourseAndTypeAndYear: async function(courseId, quizType, year, page = 0, size = 10) {
            const response = await API.request(`/quizzes/course/${courseId}/type/${quizType}/year/${year}?page=${page}&size=${size}`);
            return response;
        },
        
        /**
         * Search quizzes by title
         * @param {string} title - Quiz title to search for
         * @param {number} page - Page number
         * @param {number} size - Page size
         * @returns {Promise} Promise resolving to search results
         */
        search: async function(title, page = 0, size = 10) {
            const response = await API.request(`/quizzes/search?title=${encodeURIComponent(title)}&page=${page}&size=${size}`);
            return response;
        },
        
        /**
         * Get distinct years for quizzes
         * @returns {Promise} Promise resolving to distinct years
         */
        getDistinctYears: async function() {
            const response = await API.request('/quizzes/years');
            return response;
        },
        
        /**
         * Add questions to quiz
         * @param {number} quizId - Quiz ID
         * @param {Object} questionsData - Questions data
         * @returns {Promise} Promise resolving to updated quiz data
         */
        addQuestions: async function(quizId, questionsData) {
            const response = await API.request(`/quizzes/${quizId}/questions`, 'POST', questionsData);
            return response;
        },
        
        /**
         * Get filtered quizzes
         * @param {Object} filters - Filter criteria
         * @param {number} page - Page number
         * @param {number} size - Page size
         * @returns {Promise} Promise resolving to filtered quizzes data
         */
        getFiltered: async function(filters, page = 0, size = 10) {
            let url = `/quizzes/filter?page=${page}&size=${size}`;
            
            if (filters.courseId) {
                url += `&courseId=${filters.courseId}`;
            }
            
            if (filters.quizType) {
                url += `&quizType=${filters.quizType}`;
            }
            
            if (filters.yearGiven) {
                url += `&yearGiven=${filters.yearGiven}`;
            }
            
            if (filters.difficultyLevel) {
                url += `&difficultyLevel=${filters.difficultyLevel}`;
            }
            
            if (filters.searchTerm) {
                url += `&searchTerm=${encodeURIComponent(filters.searchTerm)}`;
            }
            
            const response = await API.request(url);
            return response;
        }
    },

    /**
     * Resource API
     */
    resource: {
        /**
         * Get all resources
         * @returns {Promise} Promise resolving to resources data
         */
        getAll: async function() {
            try {
                const response = await API.request('/academics/resources');
                return response;
            } catch (error) {
                console.error('Error getting all resources:', error);
                return { data: [] };
            }
        },
        
        /**
         * Get resource by ID
         * @param {number} id - Resource ID
         * @returns {Promise} Promise resolving to resource data
         */
        getById: async function(id) {
            try {
                const response = await API.request(`/academics/resources/${id}`);
                return response;
            } catch (error) {
                console.error(`API: Error getting resource ${id}`, error);
                throw error;
            }
        },
        
        /**
         * Delete resource
         * @param {number} id - Resource ID
         * @returns {Promise} Promise resolving to deletion response
         */
        delete: async function(id) {
            return await API.request(`/academics/resources/${id}`, 'DELETE');
        },
        
        /**
         * Get resources with pagination
         * @param {number} page - Page number
         * @param {number} size - Page size
         * @returns {Promise} Promise resolving to paginated resources data
         */
        getPaginated: async function(page = 0, size = 10) {
            try {
                const response = await API.request(`/academics/resources/paged?page=${page}&size=${size}`);
                return response.data;
            } catch (error) {
                console.error("API: Error getting paginated resources", error);
                throw error;
            }
        },
        
        /**
         * Get resources by course ID with pagination
         * @param {number} courseId - Course ID
         * @param {number} page - Page number
         * @param {number} size - Page size
         * @returns {Promise} Promise resolving to resources data
         */
        getByCourse: async function(courseId, page = 0, size = 10) {
            const response = await API.request(`/academics/resources/course/${courseId}?page=${page}&size=${size}`);
            return response.data;
        },
        
        /**
         * Get resources by department ID with pagination
         * @param {number} departmentId - Department ID
         * @param {number} page - Page number
         * @param {number} size - Page size
         * @returns {Promise} Promise resolving to resources data
         */
        getByDepartment: async function(departmentId, page = 0, size = 10) {
            const response = await API.request(`/academics/resources/department/${departmentId}?page=${page}&size=${size}`);
            return response.data;
        },
        
        /**
         * Get resources by faculty ID with pagination
         * @param {number} facultyId - Faculty ID
         * @param {number} page - Page number
         * @param {number} size - Page size
         * @returns {Promise} Promise resolving to resources data
         */
        getByFaculty: async function(facultyId, page = 0, size = 10) {
            const response = await API.request(`/academics/resources/faculty/${facultyId}?page=${page}&size=${size}`);
            return response.data;
        },
        
        /**
         * Get resources by college ID with pagination
         * @param {number} collegeId - College ID
         * @param {number} page - Page number
         * @param {number} size - Page size
         * @returns {Promise} Promise resolving to resources data
         */
        getByCollege: async function(collegeId, page = 0, size = 10) {
            const response = await API.request(`/academics/resources/college/${collegeId}?page=${page}&size=${size}`);
            return response.data;
        },
        
        /**
         * Search resources by title with pagination
         * @param {string} keyword - Search keyword
         * @param {number} page - Page number
         * @param {number} size - Page size
         * @returns {Promise} Promise resolving to search results
         */
        search: async function(keyword, page = 0, size = 10) {
            const response = await API.request(`/academics/resources/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`);
            return response.data;
        },
        
        /**
         * Search resources by title and course ID with pagination
         * @param {string} keyword - Search keyword
         * @param {number} courseId - Course ID
         * @param {number} page - Page number
         * @param {number} size - Page size
         * @returns {Promise} Promise resolving to search results
         */
        searchByCourse: async function(keyword, courseId, page = 0, size = 10) {
            const response = await API.request(`/academics/resources/search/course/${courseId}?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`);
            return response.data;
        },
        
        /**
         * Get filtered resources
         * @param {Object} filters - Filter criteria
         * @param {number} page - Page number
         * @param {number} size - Page size
         * @returns {Promise} Promise resolving to filtered resources data
         */
        getFiltered: async function(filters, page = 0, size = 10) {
            let url = `/academics/resources/filter?page=${page}&size=${size}`;
            
            if (filters.courseId) {
                url += `&courseId=${filters.courseId}`;
            }
            
            if (filters.departmentId) {
                url += `&departmentId=${filters.departmentId}`;
            }
            
            if (filters.facultyId) {
                url += `&facultyId=${filters.facultyId}`;
            }
            
            if (filters.collegeId) {
                url += `&collegeId=${filters.collegeId}`;
            }
            
            if (filters.keyword) {
                url += `&keyword=${encodeURIComponent(filters.keyword)}`;
            }
            
            const response = await API.request(url);
            return response.data;
        }
    }
};

// Export API object
window.API = API;
