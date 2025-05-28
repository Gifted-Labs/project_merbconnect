/**
 * Quiz management module for the Academic Management System
 */

const Quiz = {
    // Current page for pagination
    currentPage: 0,
    
    // Page size for pagination
    pageSize: 10,
    
    // Total number of pages
    totalPages: 0,
    
    // Current quiz being edited
    currentQuiz: null,
    
    // Current filters
    filters: {},

    /**
     * Initialize quiz module
     */
    init: function() {
        this.bindEvents();
    },

    /**
     * Bind event listeners
     */
    bindEvents: function() {
        // Navigation link
        const quizLinks = document.querySelectorAll('[data-page="quiz"]');
        quizLinks.forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                this.showQuizSection();
            });
        });
    },

    /**
     * Show quiz section
     */
    showQuizSection: function() {
        // Navigate to quiz page
        window.App.navigateTo('quiz');
        
        // Render quiz section
        this.renderQuizSection();
    },

    /**
     * Render quiz section
     */
    renderQuizSection: function() {
        const quizContainer = document.getElementById('quiz-container');
        quizContainer.innerHTML = '';

        // Create section header
        const header = document.createElement('div');
        header.className = 'flex justify-between items-center mb-6';
        header.innerHTML = `
            <h1 class="text-3xl font-bold text-gray-800">Quizzes</h1>
            <div class="flex space-x-2">
                <button id="create-quiz-btn" class="bg-primary hover:bg-blue-700 text-white px-4 py-2 rounded">
                    <i class="fas fa-plus mr-2"></i> Add Quiz
                </button>
            </div>
        `;
        quizContainer.appendChild(header);

        // Create filter section
        const filterSection = document.createElement('div');
        filterSection.className = 'bg-white rounded-lg shadow-md p-4 mb-6';
        filterSection.innerHTML = `
            <h2 class="text-lg font-semibold mb-4">Filter Quizzes</h2>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                    <label for="quiz-type-filter" class="block text-sm font-medium text-gray-700 mb-1">Quiz Type</label>
                    <select id="quiz-type-filter" class="w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                        <option value="">All Types</option>
                        <option value="MIDTERM">Midterm</option>
                        <option value="FINAL">Final</option>
                        <option value="ASSIGNMENT">Assignment</option>
                        <option value="PRACTICE">Practice</option>
                    </select>
                </div>
                <div>
                    <label for="year-filter" class="block text-sm font-medium text-gray-700 mb-1">Year</label>
                    <select id="year-filter" class="w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                        <option value="">All Years</option>
                    </select>
                </div>
                <div>
                    <label for="course-filter" class="block text-sm font-medium text-gray-700 mb-1">Course</label>
                    <select id="course-filter" class="w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                        <option value="">All Courses</option>
                    </select>
                </div>
                <div>
                    <label for="difficulty-filter" class="block text-sm font-medium text-gray-700 mb-1">Difficulty</label>
                    <select id="difficulty-filter" class="w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                        <option value="">All Difficulties</option>
                        <option value="EASY">Easy</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="HARD">Hard</option>
                    </select>
                </div>
                <div>
                    <label for="quiz-search" class="block text-sm font-medium text-gray-700 mb-1">Search</label>
                    <div class="flex">
                        <input type="text" id="quiz-search" placeholder="Search quizzes..." class="flex-grow rounded-l-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                        <button id="quiz-search-btn" class="bg-primary hover:bg-blue-700 text-white px-4 py-2 rounded-r-md">
                            Search
                        </button>
                    </div>
                </div>
            </div>
            <div class="flex justify-end mt-4">
                <button id="apply-filters-btn" class="bg-primary hover:bg-blue-700 text-white px-4 py-2 rounded">
                    Apply Filters
                </button>
                <button id="clear-filters-btn" class="ml-2 bg-gray-200 hover:bg-gray-300 text-gray-800 px-4 py-2 rounded">
                    Clear Filters
                </button>
            </div>
        `;
        quizContainer.appendChild(filterSection);

        // Create table container
        const tableContainer = document.createElement('div');
        tableContainer.className = 'bg-white rounded-lg shadow-md overflow-hidden';
        tableContainer.innerHTML = `
            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Title</th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Course</th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Year</th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Questions</th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Difficulty</th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                        </tr>
                    </thead>
                    <tbody id="quizzes-table-body" class="bg-white divide-y divide-gray-200">
                        <tr>
                            <td colspan="8" class="px-6 py-4 text-center text-gray-500">Loading quizzes...</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div id="quiz-pagination" class="px-6 py-4 bg-gray-50 flex justify-center">
                <!-- Pagination will be added here -->
            </div>
        `;
        quizContainer.appendChild(tableContainer);

        // Add event listeners
        document.getElementById('create-quiz-btn').addEventListener('click', this.showCreateQuizForm.bind(this));
        document.getElementById('quiz-search-btn').addEventListener('click', this.handleQuizSearch.bind(this));
        document.getElementById('quiz-search').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.handleQuizSearch();
            }
        });
        document.getElementById('apply-filters-btn').addEventListener('click', this.applyFilters.bind(this));
        document.getElementById('clear-filters-btn').addEventListener('click', this.clearFilters.bind(this));

        // Load filter options
        this.loadFilterOptions();
        
        // Load quizzes
        this.loadQuizzes();
    },

    /**
     * Load filter options (years, courses)
     */
    loadFilterOptions: async function() {
        try {
            // Load years
            const yearsResponse = await API.quiz.getDistinctYears();
            const yearFilter = document.getElementById('year-filter');
            
            if (yearsResponse && yearsResponse.success && yearsResponse.data) {
                yearsResponse.data.forEach(year => {
                    const option = document.createElement('option');
                    option.value = year;
                    option.textContent = year;
                    yearFilter.appendChild(option);
                });
            }
            
            // Load courses
            const coursesResponse = await API.course.getAll();
            const courseFilter = document.getElementById('course-filter');
            
            if (coursesResponse && coursesResponse.success && coursesResponse.data) {
                coursesResponse.data.forEach(course => {
                    const option = document.createElement('option');
                    option.value = course.id;
                    option.textContent = `${course.courseCode} - ${course.title}`;
                    courseFilter.appendChild(option);
                });
            }
        } catch (error) {
            console.error('Error loading filter options:', error);
        }
    },

    /**
     * Load quizzes with pagination
     */
    loadQuizzes: async function() {
        try {
            Utils.showLoading();
            
            let response;
            if (Object.keys(this.filters).length > 0) {
                response = await API.quiz.getFiltered(this.filters, this.currentPage, this.pageSize);
            } else {
                response = await API.quiz.getPaginated(this.currentPage, this.pageSize);
            }
            
            Utils.hideLoading();
            
            if (!response || !response.success) {
                console.error("Invalid response format:", response);
                this.renderEmptyQuizzes("Error: Unexpected API response format");
                return;
            }
            
            // Update pagination info
            this.totalPages = response.data.totalPages || 0;
            
            // Render quizzes
            this.renderQuizzes(response.data.content || []);
            
            // Render pagination
            this.renderPagination();
        } catch (error) {
            Utils.hideLoading();
            console.error('Error loading quizzes:', error);
            this.renderEmptyQuizzes(`Failed to load quizzes: ${error.message || 'Unknown error'}`);
        }
    },

    /**
     * Render empty quizzes message
     * @param {string} message - Message to display
     */
    renderEmptyQuizzes: function(message) {
        const tableBody = document.getElementById('quizzes-table-body');
        tableBody.innerHTML = `
            <tr>
                <td colspan="8" class="px-6 py-4 text-center text-gray-500">${message || 'No quizzes found'}</td>
            </tr>
        `;
        
        // Hide pagination
        document.getElementById('quiz-pagination').innerHTML = '';
    },

    /**
     * Render quizzes in table
     * @param {Array} quizzes - Array of quiz objects
     */
    renderQuizzes: function(quizzes) {
        const tableBody = document.getElementById('quizzes-table-body');
        tableBody.innerHTML = '';
        
        if (!quizzes || quizzes.length === 0) {
            this.renderEmptyQuizzes();
            return;
        }
        
        quizzes.forEach(quiz => {
            const row = document.createElement('tr');
            row.className = 'hover:bg-gray-50';
            row.innerHTML = `
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${quiz.id}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${quiz.title}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${quiz.quizType}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${quiz.course ? quiz.course.courseCode : 'N/A'}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${quiz.yearGiven}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${quiz.numberOfQuestions}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${quiz.difficultyLevel}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    <button class="text-primary hover:text-primary-dark mr-2 edit-quiz" data-id="${quiz.id}">Edit</button>
                    <button class="text-red-600 hover:text-red-800 delete-quiz" data-id="${quiz.id}">Delete</button>
                    <button class="text-blue-600 hover:text-blue-800 ml-2 view-quiz" data-id="${quiz.id}">View</button>
                    <button class="text-green-600 hover:text-green-800 ml-2 add-questions" data-id="${quiz.id}">Add Questions</button>
                </td>
            `;
            tableBody.appendChild(row);
            
            // Add event listeners
            row.querySelector('.edit-quiz').addEventListener('click', () => this.handleEditQuiz(quiz.id));
            row.querySelector('.delete-quiz').addEventListener('click', () => this.handleDeleteQuiz(quiz.id));
            row.querySelector('.view-quiz').addEventListener('click', () => this.handleViewQuiz(quiz.id));
            row.querySelector('.add-questions').addEventListener('click', () => this.handleAddQuestions(quiz.id));
        });
    },

    /**
     * Render pagination controls
     */
    renderPagination: function() {
        const paginationContainer = document.getElementById('quiz-pagination');
        paginationContainer.innerHTML = '';
        
        if (this.totalPages <= 1) {
            return;
        }
        
        const pagination = Utils.createPagination(this.currentPage, this.totalPages, (page) => {
            this.currentPage = page;
            this.loadQuizzes();
        });
        
        paginationContainer.appendChild(pagination);
    },

    /**
     * Show create quiz form
     */
    showCreateQuizForm: async function() {
        try {
            Utils.showLoading();
            
            // Load courses for dropdown
            const courses = await API.course.getAll();
            
            Utils.hideLoading();
            
            let courseOptions = '';
            if (courses && courses.data) {
                courses.data.forEach(course => {
                    courseOptions += `<option value="${course.id}">${course.courseCode} - ${course.title}</option>`;
                });
            }
            
            // Get current year for default value
            const currentYear = new Date().getFullYear();
            
            Swal.fire({
                title: 'Create Quiz',
                html: `
                    <form id="create-quiz-form" class="space-y-4">
                        <div>
                            <label for="quiz-title" class="block text-sm font-medium text-gray-700 text-left">Title</label>
                            <input type="text" id="quiz-title" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                        </div>
                        <div>
                            <label for="quiz-description" class="block text-sm font-medium text-gray-700 text-left">Description</label>
                            <textarea id="quiz-description" rows="3" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border"></textarea>
                        </div>
                        <div>
                            <label for="quiz-course" class="block text-sm font-medium text-gray-700 text-left">Course</label>
                            <select id="quiz-course" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                                <option value="">Select Course</option>
                                ${courseOptions}
                            </select>
                        </div>
                        <div>
                            <label for="quiz-type" class="block text-sm font-medium text-gray-700 text-left">Quiz Type</label>
                            <select id="quiz-type" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                                <option value="EXAMS_PREP">Exams Prep</option>
                                <option value="PAST_EXAMS">Past Exams</option>
                                <option value="QUIZZES">Quizzes</option>
                                <option value="ASSIGNMENTS">Assignments</option>
                                <option value="TEXTBOOK_SOLUTIONS">Textbook Solutions</option>
                            </select>
                        </div>
                        <div>
                            <label for="quiz-difficulty" class="block text-sm font-medium text-gray-700 text-left">Difficulty Level</label>
                            <select id="quiz-difficulty" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                                <option value="EASY">Easy</option>
                                <option value="MEDIUM">Medium</option>
                                <option value="HARD">Hard</option>
                            </select>
                        </div>
                        <div>
                            <label for="quiz-year" class="block text-sm font-medium text-gray-700 text-left">Year Given</label>
                            <input type="number" id="quiz-year" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border" value="${currentYear}" min="1900" max="2100">
                        </div>
                        <div>
                            <label for="quiz-num-questions" class="block text-sm font-medium text-gray-700 text-left">Number of Questions</label>
                            <input type="number" id="quiz-num-questions" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border" value="0" min="0" max="100">
                        </div>
                    </form>
                `,
                showCancelButton: true,
                confirmButtonText: 'Create',
                confirmButtonColor: '#3B82F6',
                cancelButtonText: 'Cancel',
                focusConfirm: false,
                preConfirm: () => {
                    // Validate form
                    const title = document.getElementById('quiz-title').value.trim();
                    const courseId = document.getElementById('quiz-course').value;
                    const quizType = document.getElementById('quiz-type').value;
                    const difficultyLevel = document.getElementById('quiz-difficulty').value;
                    const yearGiven = document.getElementById('quiz-year').value;
                    
                    if (!title) {
                        Swal.showValidationMessage('Title is required');
                        return false;
                    }
                    
                    if (!courseId) {
                        Swal.showValidationMessage('Course is required');
                        return false;
                    }
                    
                    if (!quizType) {
                        Swal.showValidationMessage('Quiz type is required');
                        return false;
                    }
                    
                    if (!difficultyLevel) {
                        Swal.showValidationMessage('Difficulty level is required');
                        return false;
                    }
                    
                    if (!yearGiven || yearGiven < 1900 || yearGiven > 2100) {
                        Swal.showValidationMessage('Valid year between 1900 and 2100 is required');
                        return false;
                    }
                    
                    // Create quiz object
                    return {
                        title: title,
                        description: document.getElementById('quiz-description').value.trim(),
                        courseId: parseInt(courseId),
                        quizType: quizType,
                        difficultyLevel: difficultyLevel,
                        yearGiven: parseInt(yearGiven),
                        numberOfQuestions: parseInt(document.getElementById('quiz-num-questions').value) || 0,
                        questions: [] // Empty questions array, will be added later
                    };
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    console.log('Creating quiz with data:', result.value);
                    this.createQuiz(result.value);
                }
            });
        } catch (error) {
            Utils.hideLoading();
            console.error('Error showing create quiz form:', error);
            Utils.showErrorToast('Failed to prepare quiz form');
        }
    },

    /**
     * Create a new quiz
     * @param {Object} quizData - Quiz data
     */
    createQuiz: async function(quizData) {
        try {
            console.log('Creating quiz with data:', quizData);
            Utils.showLoading();
            
            const response = await API.quiz.create(quizData);
            
            Utils.hideLoading();
            
            console.log('Create quiz response:', response);
            
            if (response && response.success) {
                Utils.showSuccessToast('Quiz created successfully');
                this.loadQuizzes();
                
                // Ask if user wants to add questions now
                Swal.fire({
                    title: 'Add Questions?',
                    text: 'Do you want to add questions to this quiz now?',
                    icon: 'question',
                    showCancelButton: true,
                    confirmButtonText: 'Yes',
                    cancelButtonText: 'No'
                }).then((result) => {
                    if (result.isConfirmed && response.data && response.data.id) {
                        this.handleAddQuestions(response.data.id);
                    }
                });
            } else {
                Utils.showErrorToast(response?.message || 'Failed to create quiz');
            }
        } catch (error) {
            Utils.hideLoading();
            console.error('Error creating quiz:', error);
            Utils.showErrorToast(`Failed to create quiz: ${error.message || 'Unknown error'}`);
        }
    },

    /**
     * Handle edit quiz button click
     * @param {number} quizId - Quiz ID
     */
    handleEditQuiz: async function(quizId) {
        try {
            console.log('Editing quiz with ID:', quizId);
            Utils.showLoading();
            
            // Load quiz details
            const quizResponse = await API.quiz.getById(quizId);
            
            // Load courses for dropdown
            const coursesResponse = await API.course.getAll();
            
            Utils.hideLoading();
            
            if (!quizResponse || !quizResponse.success) {
                Utils.showErrorToast('Failed to load quiz details');
                return;
            }
            
            const quiz = quizResponse.data;
            console.log('Loaded quiz for editing:', quiz);
            
            let courseOptions = '';
            if (coursesResponse && coursesResponse.success && coursesResponse.data) {
                coursesResponse.data.forEach(course => {
                    const selected = course.id === quiz.course.id ? 'selected' : '';
                    courseOptions += `<option value="${course.id}" ${selected}>${course.courseCode} - ${course.title}</option>`;
                });
            }
            
            Swal.fire({
                title: 'Edit Quiz',
                html: `
                    <form id="edit-quiz-form" class="space-y-4">
                        <div>
                            <label for="quiz-title" class="block text-sm font-medium text-gray-700 text-left">Title</label>
                            <input type="text" id="quiz-title" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border" value="${quiz.title}">
                        </div>
                        <div>
                            <label for="quiz-description" class="block text-sm font-medium text-gray-700 text-left">Description</label>
                            <textarea id="quiz-description" rows="3" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">${quiz.description || ''}</textarea>
                        </div>
                        <div>
                            <label for="quiz-course" class="block text-sm font-medium text-gray-700 text-left">Course</label>
                            <select id="quiz-course" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                                <option value="">Select Course</option>
                                ${courseOptions}
                            </select>
                        </div>
                        <div>
                            <label for="quiz-type" class="block text-sm font-medium text-gray-700 text-left">Quiz Type</label>
                            <select id="quiz-type" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                                <option value="EXAMS_PREP" ${quiz.quizType === 'EXAMS_PREP' ? 'selected' : ''}>Exams Prep</option>
                                <option value="PAST_EXAMS" ${quiz.quizType === 'PAST_EXAMS' ? 'selected' : ''}>Past Exams</option>
                                <option value="QUIZZES" ${quiz.quizType === 'QUIZZES' ? 'selected' : ''}>Quizzes</option>
                                <option value="ASSIGNMENTS" ${quiz.quizType === 'ASSIGNMENTS' ? 'selected' : ''}>Assignments</option>
                                <option value="TEXTBOOK_SOLUTIONS" ${quiz.quizType === 'TEXTBOOK_SOLUTIONS' ? 'selected' : ''}>Textbook Solutions</option>
                            </select>
                        </div>
                        <div>
                            <label for="quiz-difficulty" class="block text-sm font-medium text-gray-700 text-left">Difficulty Level</label>
                            <select id="quiz-difficulty" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                                <option value="EASY" ${quiz.difficultyLevel === 'EASY' ? 'selected' : ''}>Easy</option>
                                <option value="MEDIUM" ${quiz.difficultyLevel === 'MEDIUM' ? 'selected' : ''}>Medium</option>
                                <option value="HARD" ${quiz.difficultyLevel === 'HARD' ? 'selected' : ''}>Hard</option>
                            </select>
                        </div>
                        <div>
                            <label for="quiz-year" class="block text-sm font-medium text-gray-700 text-left">Year Given</label>
                            <input type="number" id="quiz-year" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border" value="${quiz.yearGiven}" min="1900" max="2100">
                        </div>
                        <div>
                            <label for="quiz-num-questions" class="block text-sm font-medium text-gray-700 text-left">Number of Questions</label>
                            <input type="number" id="quiz-num-questions" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border" value="${quiz.numberOfQuestions}" min="0" max="100">
                        </div>
                    </form>
                `,
                showCancelButton: true,
                confirmButtonText: 'Update',
                confirmButtonColor: '#3B82F6',
                cancelButtonText: 'Cancel',
                focusConfirm: false,
                preConfirm: () => {
                    // Validate form
                    const title = document.getElementById('quiz-title').value.trim();
                    const courseId = document.getElementById('quiz-course').value;
                    const quizType = document.getElementById('quiz-type').value;
                    const difficultyLevel = document.getElementById('quiz-difficulty').value;
                    const yearGiven = document.getElementById('quiz-year').value;
                    
                    if (!title) {
                        Swal.showValidationMessage('Title is required');
                        return false;
                    }
                    
                    if (!courseId) {
                        Swal.showValidationMessage('Course is required');
                        return false;
                    }
                    
                    if (!quizType) {
                        Swal.showValidationMessage('Quiz type is required');
                        return false;
                    }
                    
                    if (!difficultyLevel) {
                        Swal.showValidationMessage('Difficulty level is required');
                        return false;
                    }
                    
                    if (!yearGiven || yearGiven < 1900 || yearGiven > 2100) {
                        Swal.showValidationMessage('Valid year between 1900 and 2100 is required');
                        return false;
                    }
                    
                    // Create quiz object
                    return {
                        title: title,
                        description: document.getElementById('quiz-description').value.trim(),
                        courseId: parseInt(courseId),
                        quizType: quizType,
                        difficultyLevel: difficultyLevel,
                        yearGiven: parseInt(yearGiven),
                        numberOfQuestions: parseInt(document.getElementById('quiz-num-questions').value) || 0
                    };
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    console.log('Updating quiz with data:', result.value);
                    this.updateQuiz(quizId, result.value);
                }
            });
        } catch (error) {
            Utils.hideLoading();
            console.error('Error preparing edit quiz form:', error);
            Utils.showErrorToast('Failed to prepare edit form');
        }
    },

    /**
     * Update an existing quiz
     * @param {number} quizId - Quiz ID
     * @param {Object} quizData - Quiz data
     */
    updateQuiz: async function(quizId, quizData) {
        try {
            console.log('Updating quiz with ID:', quizId, 'and data:', quizData);
            Utils.showLoading();
            
            const response = await API.quiz.update(quizId, quizData);
            
            Utils.hideLoading();
            
            console.log('Update quiz response:', response);
            
            if (response && response.success) {
                Utils.showSuccessToast('Quiz updated successfully');
                this.loadQuizzes();
            } else {
                Utils.showErrorToast(response?.message || 'Failed to update quiz');
            }
        } catch (error) {
            Utils.hideLoading();
            console.error('Error updating quiz:', error);
            Utils.showErrorToast(`Failed to update quiz: ${error.message || 'Unknown error'}`);
        }
    },

    /**
     * Handle delete quiz button click
     * @param {number} quizId - Quiz ID
     */
    handleDeleteQuiz: function(quizId) {
        console.log('Deleting quiz with ID:', quizId);
        Swal.fire({
            title: 'Delete Quiz',
            text: 'Are you sure you want to delete this quiz? This action cannot be undone.',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Delete',
            confirmButtonColor: '#EF4444',
            cancelButtonText: 'Cancel',
            focusCancel: true
        }).then((result) => {
            if (result.isConfirmed) {
                this.deleteQuiz(quizId);
            }
        });
    },

    /**
     * Delete a quiz
     * @param {number} quizId - Quiz ID
     */
    deleteQuiz: async function(quizId) {
        try {
            console.log('Deleting quiz with ID:', quizId);
            Utils.showLoading();
            
            const response = await API.quiz.delete(quizId);
            
            Utils.hideLoading();
            
            console.log('Delete quiz response:', response);
            
            if (response && response.success) {
                Utils.showSuccessToast('Quiz deleted successfully');
                this.loadQuizzes();
            } else {
                Utils.showErrorToast(response?.message || 'Failed to delete quiz');
            }
        } catch (error) {
            Utils.hideLoading();
            console.error('Error deleting quiz:', error);
            Utils.showErrorToast(`Failed to delete quiz: ${error.message || 'Unknown error'}`);
        }
    },

    /**
     * Handle view quiz button click
     * @param {number} quizId - Quiz ID
     */
    handleViewQuiz: async function(quizId) {
        try {
            console.log('Viewing quiz with ID:', quizId);
            Utils.showLoading();
            
            const response = await API.quiz.getById(quizId);
            
            Utils.hideLoading();
            
            console.log('View quiz response:', response);
            
            if (!response || !response.success) {
                Utils.showErrorToast('Failed to load quiz details');
                return;
            }
            
            const quiz = response.data;
            
            // Generate HTML for questions
            let questionsHtml = '';
            if (quiz.questions && quiz.questions.length > 0) {
                questionsHtml = `
                    <div class="mt-4">
                        <h3 class="text-lg font-semibold mb-2">Questions (${quiz.questions.length})</h3>
                        <div class="space-y-4">
                `;
                
                quiz.questions.forEach((question, index) => {
                    let optionsHtml = '';
                    if (question.possibleAnswers && question.possibleAnswers.length > 0) {
                        optionsHtml = '<ul class="list-disc pl-5 mt-2">';
                        question.possibleAnswers.forEach(option => {
                            const isCorrect = option === question.correctAnswer;
                            optionsHtml += `
                                <li class="${isCorrect ? 'font-semibold text-green-600' : ''}">
                                    ${option} ${isCorrect ? '(Correct)' : ''}
                                </li>
                            `;
                        });
                        optionsHtml += '</ul>';
                    }
                    
                    questionsHtml += `
                        <div class="p-3 border rounded">
                            <p class="font-medium">Q${index + 1}: ${question.questionText}</p>
                            ${optionsHtml}
                        </div>
                    `;
                });
                
                questionsHtml += '</div></div>';
            } else {
                questionsHtml = '<p class="mt-4 text-gray-500">No questions added to this quiz yet.</p>';
            }
            
            // Show quiz details
            const quizDetails = `
                <div class="text-left">
                    <h2 class="text-xl font-bold mb-4">${quiz.title}</h2>
                    <p class="mb-4">${quiz.description || 'No description'}</p>
                    <p class="mb-2"><strong>Course:</strong> ${quiz.course ? `${quiz.course.courseCode} - ${quiz.course.title}` : 'N/A'}</p>
                    <p class="mb-2"><strong>Type:</strong> ${quiz.quizType}</p>
                    <p class="mb-2"><strong>Difficulty:</strong> ${quiz.difficultyLevel}</p>
                    <p class="mb-2"><strong>Year Given:</strong> ${quiz.yearGiven}</p>
                    ${questionsHtml}
                </div>
            `;
            
            Swal.fire({
                title: 'Quiz Details',
                html: quizDetails,
                showCloseButton: true,
                showConfirmButton: false,
                focusConfirm: false,
                width: '800px'
            });
        } catch (error) {
            Utils.hideLoading();
            console.error('Error loading quiz details:', error);
            Utils.showErrorToast('Failed to load quiz details');
        }
    },

    /**
     * Handle add questions button click
     * @param {number} quizId - Quiz ID
     */
    handleAddQuestions: async function(quizId) {
        try {
            console.log('Adding questions to quiz with ID:', quizId);
            Utils.showLoading();
            
            // Load quiz details
            const response = await API.quiz.getById(quizId);
            
            Utils.hideLoading();
            
            if (!response || !response.success) {
                Utils.showErrorToast('Failed to load quiz details');
                return;
            }
            
            const quiz = response.data;
            console.log('Loaded quiz for adding questions:', quiz);
            
            // Create a form for adding questions
            Swal.fire({
                title: 'Add Questions to Quiz',
                html: `
                    <div class="text-left mb-4">
                        <p><strong>Quiz:</strong> ${quiz.title}</p>
                    </div>
                    <form id="add-questions-form" class="space-y-4">
                        <div id="questions-container">
                            <div class="question-item border rounded p-3 mb-4">
                                <div>
                                    <label class="block text-sm font-medium text-gray-700 text-left">Question Text</label>
                                    <textarea class="question-text mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border" rows="2"></textarea>
                                </div>
                                <div class="options-container mt-3">
                                    <label class="block text-sm font-medium text-gray-700 text-left mb-2">Options</label>
                                    <div class="option-item flex items-center mb-2">
                                        <input type="radio" name="correct-0" class="correct-option mr-2" checked>
                                        <input type="text" class="option-text flex-grow rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border" placeholder="Option text">
                                        <button type="button" class="remove-option ml-2 text-red-500 hover:text-red-700">
                                            <i class="fas fa-times"></i>
                                        </button>
                                    </div>
                                    <div class="option-item flex items-center mb-2">
                                        <input type="radio" name="correct-0" class="correct-option mr-2">
                                        <input type="text" class="option-text flex-grow rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border" placeholder="Option text">
                                        <button type="button" class="remove-option ml-2 text-red-500 hover:text-red-700">
                                            <i class="fas fa-times"></i>
                                        </button>
                                    </div>
                                    <button type="button" class="add-option text-sm text-blue-600 hover:text-blue-800">
                                        <i class="fas fa-plus mr-1"></i> Add Option
                                    </button>
                                </div>
                                <div class="flex justify-end mt-2">
                                    <button type="button" class="remove-question text-sm text-red-600 hover:text-red-800">
                                        <i class="fas fa-trash mr-1"></i> Remove Question
                                    </button>
                                </div>
                            </div>
                        </div>
                        <button type="button" id="add-question-btn" class="w-full bg-blue-100 hover:bg-blue-200 text-blue-800 py-2 px-4 rounded">
                            <i class="fas fa-plus mr-1"></i> Add Another Question
                        </button>
                    </form>
                `,
                showCancelButton: true,
                confirmButtonText: 'Save Questions',
                confirmButtonColor: '#3B82F6',
                cancelButtonText: 'Cancel',
                focusConfirm: false,
                width: '800px',
                didOpen: () => {
                    // Add event listener for adding new questions
                    document.getElementById('add-question-btn').addEventListener('click', () => {
                        const questionsContainer = document.getElementById('questions-container');
                        const questionCount = questionsContainer.querySelectorAll('.question-item').length;

                        const newQuestion = document.createElement('div');
                        newQuestion.className = 'question-item border rounded p-3 mb-4';
                        newQuestion.innerHTML = `
                            <div>
                                <label class="block text-sm font-medium text-gray-700 text-left">Question Text</label>
                                <textarea class="question-text mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border" rows="2"></textarea>
                            </div>
                            <div class="options-container mt-3">
                                <label class="block text-sm font-medium text-gray-700 text-left mb-2">Options</label>
                                <div class="option-item flex items-center mb-2">
                                    <input type="radio" name="correct-${questionCount}" class="correct-option mr-2" checked>
                                    <input type="text" class="option-text flex-grow rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border" placeholder="Option text">
                                    <button type="button" class="remove-option ml-2 text-red-500 hover:text-red-700">
                                        <i class="fas fa-times"></i>
                                    </button>
                                </div>
                                <div class="option-item flex items-center mb-2">
                                    <input type="radio" name="correct-${questionCount}" class="correct-option mr-2">
                                    <input type="text" class="option-text flex-grow rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border" placeholder="Option text">
                                    <button type="button" class="remove-option ml-2 text-red-500 hover:text-red-700">
                                        <i class="fas fa-times"></i>
                                    </button>
                                </div>
                                <button type="button" class="add-option text-sm text-blue-600 hover:text-blue-800">
                                    <i class="fas fa-plus mr-1"></i> Add Option
                                </button>
                            </div>
                            <div class="flex justify-end mt-2">
                                <button type="button" class="remove-question text-sm text-red-600 hover:text-red-800">
                                    <i class="fas fa-trash mr-1"></i> Remove Question
                                </button>
                            </div>
                        `;

                        questionsContainer.appendChild(newQuestion);

                        // Add event listeners for the new question
                        this.addQuestionEventListeners(newQuestion);
                    });

                    // Add event listeners for initial question
                    const initialQuestion = document.querySelector('.question-item');
                    this.addQuestionEventListeners(initialQuestion);
                },
                preConfirm: () => {
                    // Validate and collect questions
                    const questions = [];
                    const questionItems = document.querySelectorAll('.question-item');

                    for (let i = 0; i < questionItems.length; i++) {
                        const item = questionItems[i];
                        const questionText = item.querySelector('.question-text').value.trim();

                        if (!questionText) {
                            Swal.showValidationMessage(`Question ${i + 1} text is required`);
                            return false;
                        }

                        const optionItems = item.querySelectorAll('.option-item');
                        if (optionItems.length < 2) {
                            Swal.showValidationMessage(`Question ${i + 1} must have at least 2 options`);
                            return false;
                        }

                        const options = [];
                        let hasCorrectOption = false;

                        for (let j = 0; j < optionItems.length; j++) {
                            const optionItem = optionItems[j];
                            const optionText = optionItem.querySelector('.option-text').value.trim();
                            const isCorrect = optionItem.querySelector('.correct-option').checked;

                            if (!optionText) {
                                Swal.showValidationMessage(`Option ${j + 1} in Question ${i + 1} is required`);
                                return false;
                            }

                            if (isCorrect) {
                                hasCorrectOption = true;
                            }

                            options.push({
                                optionText,
                                correct: isCorrect
                            });
                        }

                        if (!hasCorrectOption) {
                            Swal.showValidationMessage(`Question ${i + 1} must have a correct option selected`);
                            return false;
                        }

                        questions.push({
                            questionText,
                            options
                        });
                    }

                    return questions;
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    this.saveQuestions(quizId, result.value);
                }
            });
        } catch (error) {
            Utils.hideLoading();
            console.error('Error preparing add questions form:', error);
            Utils.showErrorToast('Failed to prepare add questions form');
        }
    },

    /**
     * Add event listeners to question item
     * @param {HTMLElement} questionItem - Question item element
     */
    addQuestionEventListeners: function(questionItem) {
        // Add option button
        const addOptionBtn = questionItem.querySelector('.add-option');
        addOptionBtn.addEventListener('click', () => {
            const optionsContainer = questionItem.querySelector('.options-container');
            const optionItems = optionsContainer.querySelectorAll('.option-item');
            const radioName = optionItems[0].querySelector('.correct-option').name;

            const newOption = document.createElement('div');
            newOption.className = 'option-item flex items-center mb-2';
            newOption.innerHTML = `
                <input type="radio" name="${radioName}" class="correct-option mr-2">
                <input type="text" class="option-text flex-grow rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border" placeholder="Option text">
                <button type="button" class="remove-option ml-2 text-red-500 hover:text-red-700">
                    <i class="fas fa-times"></i>
                </button>
            `;

            // Insert before the add option button
            optionsContainer.insertBefore(newOption, addOptionBtn);

            // Add event listener for remove option
            const removeOptionBtn = newOption.querySelector('.remove-option');
            removeOptionBtn.addEventListener('click', () => {
                const optionItems = optionsContainer.querySelectorAll('.option-item');
                if (optionItems.length > 2) {
                    newOption.remove();
                } else {
                    Utils.showErrorToast('A question must have at least 2 options');
                }
            });
        });

        // Remove option buttons
        const removeOptionBtns = questionItem.querySelectorAll('.remove-option');
        removeOptionBtns.forEach(btn => {
            btn.addEventListener('click', () => {
                const optionsContainer = questionItem.querySelector('.options-container');
                const optionItems = optionsContainer.querySelectorAll('.option-item');
                if (optionItems.length > 2) {
                    btn.closest('.option-item').remove();
                } else {
                    Utils.showErrorToast('A question must have at least 2 options');
                }
            });
        });

        // Remove question button
        const removeQuestionBtn = questionItem.querySelector('.remove-question');
        removeQuestionBtn.addEventListener('click', () => {
            const questionsContainer = document.getElementById('questions-container');
            const questionItems = questionsContainer.querySelectorAll('.question-item');
            if (questionItems.length > 1) {
                questionItem.remove();
            } else {
                Utils.showErrorToast('You must have at least one question');
            }
        });
    },

    /**
     * Save questions to quiz
     * @param {number} quizId - Quiz ID
     * @param {Array} questions - Array of question objects
     */
    saveQuestions: async function(quizId, questions) {
        try {
            Utils.showLoading();

            const request = {
                questions: questions
            };

            const response = await API.quiz.addQuestions(quizId, request);

            Utils.hideLoading();

            if (response && response.success) {
                Utils.showSuccessToast('Questions added successfully');
                this.loadQuizzes();
            } else {
                Utils.showErrorToast(response.message || 'Failed to add questions');
            }
        } catch (error) {
            Utils.hideLoading();
            console.error('Error adding questions:', error);
            Utils.showErrorToast('Failed to add questions');
        }
    },

    /**
     * Handle quiz search
     */
    handleQuizSearch: function() {
        const searchTerm = document.getElementById('quiz-search').value.trim();

        if (searchTerm) {
            this.filters.searchTerm = searchTerm;
        } else {
            delete this.filters.searchTerm;
        }

        this.currentPage = 0;
        this.loadQuizzes();
    },

    /**
     * Apply filters
     */
    applyFilters: function() {
        // Get filter values
        const quizType = document.getElementById('quiz-type-filter').value;
        const year = document.getElementById('year-filter').value;
        const courseId = document.getElementById('course-filter').value;
        const difficulty = document.getElementById('difficulty-filter').value;

        // Reset filters
        this.filters = {};

        // Apply filters if values are selected
        if (quizType) {
            this.filters.quizType = quizType;
        }

        if (year) {
            this.filters.yearGiven = parseInt(year);
        }

        if (courseId) {
            this.filters.courseId = parseInt(courseId);
        }

        if (difficulty) {
            this.filters.difficultyLevel = difficulty;
        }

        // Reset to first page
        this.currentPage = 0;

        // Load quizzes with filters
        this.loadQuizzes();
    },

    /**
     * Clear filters
     */
    clearFilters: function() {
        // Reset filter form
        document.getElementById('quiz-type-filter').value = '';
        document.getElementById('year-filter').value = '';
        document.getElementById('course-filter').value = '';
        document.getElementById('difficulty-filter').value = '';
        document.getElementById('quiz-search').value = '';

        // Clear filters object
        this.filters = {};

        // Reset to first page
        this.currentPage = 0;

        // Load quizzes without filters
        this.loadQuizzes();
    }
};

// Initialize the Quiz module
Quiz.init();
