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
                    <button class="text-purple-600 hover:text-purple-800 ml-2 take-quiz" data-id="${quiz.id}">Take Quiz</button>
                </td>
            `;
            tableBody.appendChild(row);

            // Add event listeners
            row.querySelector('.edit-quiz').addEventListener('click', () => this.handleEditQuiz(quiz.id));
            row.querySelector('.delete-quiz').addEventListener('click', () => this.handleDeleteQuiz(quiz.id));
            row.querySelector('.view-quiz').addEventListener('click', () => this.handleViewQuiz(quiz.id));
            row.querySelector('.add-questions').addEventListener('click', () => this.handleAddQuestions(quiz.id));
            row.querySelector('.take-quiz').addEventListener('click', () => this.handleTakeQuiz(quiz.id));
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
    },

    /**
     * Handle take quiz button click
     * @param {number} quizId - Quiz ID
     */
    handleTakeQuiz: async function(quizId) {
        try {
            console.log('Taking quiz with ID:', quizId);
            Utils.showLoading();

            const response = await API.quiz.getById(quizId);

            Utils.hideLoading();

            if (!response || !response.success) {
                Utils.showErrorToast('Failed to load quiz');
                return;
            }

            const quiz = response.data;

            if (!quiz.questions || quiz.questions.length === 0) {
                Utils.showErrorToast('This quiz has no questions yet');
                return;
            }

            // Initialize quiz taking session
            this.initializeQuizTaking(quiz);
        } catch (error) {
            Utils.hideLoading();
            console.error('Error loading quiz for taking:', error);
            Utils.showErrorToast('Failed to load quiz');
        }
    },

    /**
     * Initialize quiz taking session
     * @param {Object} quiz - Quiz object with questions
     */
    initializeQuizTaking: function(quiz) {
        // Quiz taking state
        this.quizSession = {
            quiz: quiz,
            currentQuestionIndex: 0,
            answers: {},
            startTime: new Date(),
            timeLimit: 30 * 60 * 1000, // 30 minutes in milliseconds
            timer: null
        };

        // Navigate to quiz taking interface
        window.App.navigateTo('quiz-taking');

        // Render quiz taking interface
        this.renderQuizTakingInterface();

        // Start timer
        this.startQuizTimer();
    },

    /**
     * Render quiz taking interface
     */
    renderQuizTakingInterface: function() {
        const container = document.getElementById('quiz-taking-container');
        const quiz = this.quizSession.quiz;
        const totalQuestions = quiz.questions.length;

        container.innerHTML = `
            <div class="max-w-4xl mx-auto">
                <!-- Quiz Header -->
                <div class="bg-white rounded-2xl shadow-apple-md p-6 mb-6 border border-gray-100">
                    <div class="flex justify-between items-center mb-4">
                        <div>
                            <h1 class="text-2xl font-bold text-gray-900">${quiz.title}</h1>
                            <p class="text-gray-600">${quiz.course ? quiz.course.courseCode + ' - ' + quiz.course.title : 'General Quiz'}</p>
                        </div>
                        <div class="text-right">
                            <div id="quiz-timer" class="quiz-timer">30:00</div>
                            <p class="text-sm text-gray-600 mt-1">Time Remaining</p>
                        </div>
                    </div>

                    <!-- Progress Bar -->
                    <div class="mb-4">
                        <div class="flex justify-between text-sm text-gray-600 mb-2">
                            <span>Question <span id="current-question-num">1</span> of ${totalQuestions}</span>
                            <span id="progress-percentage">0%</span>
                        </div>
                        <div class="w-full bg-gray-200 rounded-full h-2">
                            <div id="progress-bar" class="quiz-progress-bar h-2 rounded-full" style="width: ${(1/totalQuestions)*100}%"></div>
                        </div>
                    </div>
                </div>

                <!-- Question Card -->
                <div id="question-card" class="quiz-question-card p-8 mb-6">
                    <!-- Question content will be rendered here -->
                </div>

                <!-- Navigation -->
                <div class="flex justify-between items-center">
                    <button id="prev-question" class="btn btn-outline" disabled>
                        <i class="fas fa-chevron-left mr-2"></i> Previous
                    </button>

                    <div class="flex space-x-2">
                        <button id="flag-question" class="btn btn-ghost">
                            <i class="fas fa-flag mr-2"></i> Flag
                        </button>
                        <button id="review-answers" class="btn btn-outline">
                            <i class="fas fa-list mr-2"></i> Review
                        </button>
                    </div>

                    <button id="next-question" class="btn btn-primary">
                        Next <i class="fas fa-chevron-right ml-2"></i>
                    </button>
                </div>
            </div>
        `;

        // Add event listeners
        document.getElementById('prev-question').addEventListener('click', () => this.previousQuestion());
        document.getElementById('next-question').addEventListener('click', () => this.nextQuestion());
        document.getElementById('flag-question').addEventListener('click', () => this.toggleQuestionFlag());
        document.getElementById('review-answers').addEventListener('click', () => this.showReviewModal());

        // Render first question
        this.renderCurrentQuestion();
    },

    /**
     * Render current question
     */
    renderCurrentQuestion: function() {
        const questionCard = document.getElementById('question-card');
        const quiz = this.quizSession.quiz;
        const currentIndex = this.quizSession.currentQuestionIndex;
        const question = quiz.questions[currentIndex];
        const totalQuestions = quiz.questions.length;

        // Update progress
        document.getElementById('current-question-num').textContent = currentIndex + 1;
        document.getElementById('progress-percentage').textContent = Math.round(((currentIndex + 1) / totalQuestions) * 100) + '%';
        document.getElementById('progress-bar').style.width = ((currentIndex + 1) / totalQuestions) * 100 + '%';

        // Update navigation buttons
        document.getElementById('prev-question').disabled = currentIndex === 0;
        const nextBtn = document.getElementById('next-question');
        if (currentIndex === totalQuestions - 1) {
            nextBtn.innerHTML = 'Submit Quiz <i class="fas fa-check ml-2"></i>';
            nextBtn.className = 'btn btn-secondary';
        } else {
            nextBtn.innerHTML = 'Next <i class="fas fa-chevron-right ml-2"></i>';
            nextBtn.className = 'btn btn-primary';
        }

        // Render question
        let optionsHtml = '';
        if (question.possibleAnswers && question.possibleAnswers.length > 0) {
            question.possibleAnswers.forEach((option, index) => {
                const isSelected = this.quizSession.answers[currentIndex] === option;
                optionsHtml += `
                    <div class="quiz-option ${isSelected ? 'selected' : ''}" data-option="${option}">
                        <div class="flex items-center">
                            <div class="w-6 h-6 rounded-full border-2 border-current flex items-center justify-center mr-4 flex-shrink-0">
                                <div class="w-3 h-3 rounded-full bg-current ${isSelected ? 'opacity-100' : 'opacity-0'} transition-opacity duration-200"></div>
                            </div>
                            <span class="text-gray-900">${option}</span>
                        </div>
                    </div>
                `;
            });
        }

        questionCard.innerHTML = `
            <div class="mb-6">
                <h2 class="text-xl font-semibold text-gray-900 mb-4">
                    Question ${currentIndex + 1}
                </h2>
                <p class="text-gray-800 text-lg leading-relaxed">${question.questionText}</p>
            </div>

            <div class="space-y-3">
                ${optionsHtml}
            </div>
        `;

        // Add click handlers for options
        questionCard.querySelectorAll('.quiz-option').forEach(option => {
            option.addEventListener('click', () => {
                const selectedOption = option.dataset.option;
                this.selectAnswer(currentIndex, selectedOption);

                // Update UI
                questionCard.querySelectorAll('.quiz-option').forEach(opt => opt.classList.remove('selected'));
                option.classList.add('selected');
            });
        });
    },

    /**
     * Select answer for current question
     * @param {number} questionIndex - Question index
     * @param {string} answer - Selected answer
     */
    selectAnswer: function(questionIndex, answer) {
        this.quizSession.answers[questionIndex] = answer;
    },

    /**
     * Go to previous question
     */
    previousQuestion: function() {
        if (this.quizSession.currentQuestionIndex > 0) {
            this.quizSession.currentQuestionIndex--;
            this.renderCurrentQuestion();
        }
    },

    /**
     * Go to next question or submit quiz
     */
    nextQuestion: function() {
        const totalQuestions = this.quizSession.quiz.questions.length;

        if (this.quizSession.currentQuestionIndex < totalQuestions - 1) {
            this.quizSession.currentQuestionIndex++;
            this.renderCurrentQuestion();
        } else {
            this.submitQuiz();
        }
    },

    /**
     * Start quiz timer
     */
    startQuizTimer: function() {
        const timerElement = document.getElementById('quiz-timer');

        this.quizSession.timer = setInterval(() => {
            const elapsed = new Date() - this.quizSession.startTime;
            const remaining = Math.max(0, this.quizSession.timeLimit - elapsed);

            if (remaining === 0) {
                this.submitQuiz();
                return;
            }

            const minutes = Math.floor(remaining / 60000);
            const seconds = Math.floor((remaining % 60000) / 1000);

            timerElement.textContent = `${minutes}:${seconds.toString().padStart(2, '0')}`;

            // Add warning class when less than 5 minutes
            if (remaining < 5 * 60 * 1000) {
                timerElement.classList.add('warning');
            }
        }, 1000);
    },

    /**
     * Submit quiz
     */
    submitQuiz: function() {
        // Clear timer
        if (this.quizSession.timer) {
            clearInterval(this.quizSession.timer);
        }

        // Calculate results
        const quiz = this.quizSession.quiz;
        const answers = this.quizSession.answers;
        let correctAnswers = 0;
        const totalQuestions = quiz.questions.length;
        const results = [];

        quiz.questions.forEach((question, index) => {
            const userAnswer = answers[index];
            const correctAnswer = question.correctAnswer;
            const isCorrect = userAnswer === correctAnswer;

            if (isCorrect) {
                correctAnswers++;
            }

            results.push({
                question: question,
                userAnswer: userAnswer,
                correctAnswer: correctAnswer,
                isCorrect: isCorrect
            });
        });

        const score = Math.round((correctAnswers / totalQuestions) * 100);
        const endTime = new Date();
        const timeTaken = Math.round((endTime - this.quizSession.startTime) / 1000);

        // Show results
        this.showQuizResults({
            quiz: quiz,
            results: results,
            score: score,
            correctAnswers: correctAnswers,
            totalQuestions: totalQuestions,
            timeTaken: timeTaken
        });
    },

    /**
     * Show quiz results
     * @param {Object} resultData - Quiz result data
     */
    showQuizResults: function(resultData) {
        // Navigate to results container
        window.App.navigateTo('quiz-results');

        const container = document.getElementById('quiz-results-container');
        const { quiz, results, score, correctAnswers, totalQuestions, timeTaken } = resultData;

        // Determine grade and color
        let grade, gradeColor;
        if (score >= 90) {
            grade = 'A';
            gradeColor = 'text-green-600';
        } else if (score >= 80) {
            grade = 'B';
            gradeColor = 'text-blue-600';
        } else if (score >= 70) {
            grade = 'C';
            gradeColor = 'text-yellow-600';
        } else if (score >= 60) {
            grade = 'D';
            gradeColor = 'text-orange-600';
        } else {
            grade = 'F';
            gradeColor = 'text-red-600';
        }

        const minutes = Math.floor(timeTaken / 60);
        const seconds = timeTaken % 60;

        container.innerHTML = `
            <div class="max-w-4xl mx-auto">
                <!-- Results Header -->
                <div class="bg-white rounded-2xl shadow-apple-md p-8 mb-6 border border-gray-100 text-center">
                    <div class="w-20 h-20 mx-auto mb-4 bg-primary/10 rounded-full flex items-center justify-center">
                        <i class="fas fa-trophy text-primary text-2xl"></i>
                    </div>
                    <h1 class="text-3xl font-bold text-gray-900 mb-2">Quiz Completed!</h1>
                    <p class="text-gray-600 mb-6">${quiz.title}</p>

                    <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
                        <div class="text-center">
                            <div class="text-3xl font-bold ${gradeColor} mb-1">${grade}</div>
                            <div class="text-sm text-gray-600">Grade</div>
                        </div>
                        <div class="text-center">
                            <div class="text-3xl font-bold text-gray-900 mb-1">${score}%</div>
                            <div class="text-sm text-gray-600">Score</div>
                        </div>
                        <div class="text-center">
                            <div class="text-3xl font-bold text-gray-900 mb-1">${correctAnswers}/${totalQuestions}</div>
                            <div class="text-sm text-gray-600">Correct</div>
                        </div>
                        <div class="text-center">
                            <div class="text-3xl font-bold text-gray-900 mb-1">${minutes}:${seconds.toString().padStart(2, '0')}</div>
                            <div class="text-sm text-gray-600">Time</div>
                        </div>
                    </div>
                </div>

                <!-- Detailed Results -->
                <div class="bg-white rounded-2xl shadow-apple-md p-6 mb-6 border border-gray-100">
                    <h2 class="text-xl font-semibold text-gray-900 mb-6">Review Answers</h2>
                    <div class="space-y-6">
                        ${results.map((result, index) => `
                            <div class="border-l-4 ${result.isCorrect ? 'border-green-500' : 'border-red-500'} pl-4">
                                <div class="flex items-start justify-between mb-2">
                                    <h3 class="font-medium text-gray-900">Question ${index + 1}</h3>
                                    <span class="text-sm ${result.isCorrect ? 'text-green-600' : 'text-red-600'} font-medium">
                                        ${result.isCorrect ? 'Correct' : 'Incorrect'}
                                    </span>
                                </div>
                                <p class="text-gray-700 mb-3">${result.question.questionText}</p>
                                <div class="space-y-2">
                                    <div class="text-sm">
                                        <span class="font-medium text-gray-600">Your answer:</span>
                                        <span class="${result.isCorrect ? 'text-green-600' : 'text-red-600'}">${result.userAnswer || 'Not answered'}</span>
                                    </div>
                                    ${!result.isCorrect ? `
                                        <div class="text-sm">
                                            <span class="font-medium text-gray-600">Correct answer:</span>
                                            <span class="text-green-600">${result.correctAnswer}</span>
                                        </div>
                                    ` : ''}
                                </div>
                            </div>
                        `).join('')}
                    </div>
                </div>

                <!-- Actions -->
                <div class="flex justify-center space-x-4">
                    <button id="retake-quiz" class="btn btn-primary">
                        <i class="fas fa-redo mr-2"></i> Retake Quiz
                    </button>
                    <button id="back-to-quizzes" class="btn btn-outline">
                        <i class="fas fa-arrow-left mr-2"></i> Back to Quizzes
                    </button>
                </div>
            </div>
        `;

        // Add event listeners
        document.getElementById('retake-quiz').addEventListener('click', () => {
            this.initializeQuizTaking(quiz);
        });

        document.getElementById('back-to-quizzes').addEventListener('click', () => {
            window.App.navigateTo('quiz');
        });
    },

    /**
     * Toggle question flag
     */
    toggleQuestionFlag: function() {
        // Implementation for flagging questions for review
        const flagBtn = document.getElementById('flag-question');
        const currentIndex = this.quizSession.currentQuestionIndex;

        if (!this.quizSession.flaggedQuestions) {
            this.quizSession.flaggedQuestions = new Set();
        }

        if (this.quizSession.flaggedQuestions.has(currentIndex)) {
            this.quizSession.flaggedQuestions.delete(currentIndex);
            flagBtn.innerHTML = '<i class="fas fa-flag mr-2"></i> Flag';
            flagBtn.classList.remove('text-orange-600');
        } else {
            this.quizSession.flaggedQuestions.add(currentIndex);
            flagBtn.innerHTML = '<i class="fas fa-flag mr-2"></i> Flagged';
            flagBtn.classList.add('text-orange-600');
        }
    },

    /**
     * Show review modal
     */
    showReviewModal: function() {
        const quiz = this.quizSession.quiz;
        const answers = this.quizSession.answers;
        const flagged = this.quizSession.flaggedQuestions || new Set();

        let reviewHtml = '';
        quiz.questions.forEach((question, index) => {
            const answered = answers.hasOwnProperty(index);
            const isFlagged = flagged.has(index);

            reviewHtml += `
                <div class="flex items-center justify-between p-3 border rounded-lg cursor-pointer hover:bg-gray-50" data-question="${index}">
                    <div class="flex items-center space-x-3">
                        <span class="w-8 h-8 rounded-full ${answered ? 'bg-green-100 text-green-600' : 'bg-gray-100 text-gray-400'} flex items-center justify-center text-sm font-medium">
                            ${index + 1}
                        </span>
                        <span class="text-gray-900">Question ${index + 1}</span>
                    </div>
                    <div class="flex items-center space-x-2">
                        ${isFlagged ? '<i class="fas fa-flag text-orange-500"></i>' : ''}
                        ${answered ? '<i class="fas fa-check text-green-500"></i>' : '<i class="fas fa-circle text-gray-300"></i>'}
                    </div>
                </div>
            `;
        });

        Swal.fire({
            title: 'Review Questions',
            html: `
                <div class="text-left space-y-2 max-h-96 overflow-y-auto">
                    ${reviewHtml}
                </div>
            `,
            showCloseButton: true,
            showConfirmButton: false,
            width: '600px',
            didOpen: () => {
                // Add click handlers for question navigation
                document.querySelectorAll('[data-question]').forEach(item => {
                    item.addEventListener('click', () => {
                        const questionIndex = parseInt(item.dataset.question);
                        this.quizSession.currentQuestionIndex = questionIndex;
                        this.renderCurrentQuestion();
                        Swal.close();
                    });
                });
            }
        });
    }
};

// Initialize the Quiz module
Quiz.init();
