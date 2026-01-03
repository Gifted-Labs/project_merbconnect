/**
 * Resource management module for the Academic Management System
 */

const Resource = {
    // Current page for pagination
    currentPage: 0,
    
    // Page size for pagination
    pageSize: 10,
    
    // Total number of pages
    totalPages: 0,
    
    // Current resource being edited
    currentResource: null,
    
    // Current filters
    filters: {},

    /**
     * Initialize resource module
     */
    init: function() {
        this.bindEvents();
    },

    /**
     * Bind event listeners
     */
    bindEvents: function() {
        // Navigation link
        const resourceLinks = document.querySelectorAll('[data-page="resource"]');
        resourceLinks.forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                this.showResourceSection();
            });
        });
    },

    /**
     * Show resource section
     */
    showResourceSection: function() {
        // Navigate to resource page
        window.App.navigateTo('resource');
        
        // Render resource section
        this.renderResourceSection();
    },

    /**
     * Render resource section
     */
    renderResourceSection: function() {
        const resourceContainer = document.getElementById('resource-container');
        resourceContainer.innerHTML = '';

        // Create section header
        const header = document.createElement('div');
        header.className = 'flex justify-between items-center mb-6';
        header.innerHTML = `
            <h1 class="text-3xl font-bold text-gray-800">Resources</h1>
            <div class="flex space-x-2">
                <button id="create-resource-btn" class="bg-primary hover:bg-blue-700 text-white px-4 py-2 rounded">
                    <i class="fas fa-plus mr-2"></i> Add Resource
                </button>
            </div>
        `;
        resourceContainer.appendChild(header);

        // Create filter section
        const filterSection = document.createElement('div');
        filterSection.className = 'bg-white rounded-lg shadow-md p-4 mb-6';
        filterSection.innerHTML = `
            <h2 class="text-lg font-semibold mb-4">Filter Resources</h2>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                    <label for="resource-type-filter" class="block text-sm font-medium text-gray-700 mb-1">Resource Type</label>
                    <select id="resource-type-filter" class="w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                        <option value="">All Types</option>
                        <option value="QUIZ">Quiz</option>
                        <option value="BOOK">Book</option>
                        <option value="ARTICLE">Article</option>
                        <option value="VIDEO">Video</option>
                        <option value="OTHER">Other</option>
                    </select>
                </div>
                <div>
                    <label for="course-filter" class="block text-sm font-medium text-gray-700 mb-1">Course</label>
                    <select id="course-filter" class="w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                        <option value="">All Courses</option>
                    </select>
                </div>
                <div>
                    <label for="resource-search" class="block text-sm font-medium text-gray-700 mb-1">Search</label>
                    <div class="flex">
                        <input type="text" id="resource-search" placeholder="Search resources..." class="flex-grow rounded-l-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                        <button id="resource-search-btn" class="bg-primary hover:bg-blue-700 text-white px-4 py-2 rounded-r-md">
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
        resourceContainer.appendChild(filterSection);

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
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                        </tr>
                    </thead>
                    <tbody id="resources-table-body" class="bg-white divide-y divide-gray-200">
                        <tr>
                            <td colspan="5" class="px-6 py-4 text-center text-gray-500">Loading resources...</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div id="resource-pagination" class="px-6 py-4 bg-gray-50 flex justify-center">
                <!-- Pagination will be added here -->
            </div>
        `;
        resourceContainer.appendChild(tableContainer);

        // Add event listeners
        document.getElementById('create-resource-btn').addEventListener('click', this.showCreateResourceForm.bind(this));
        document.getElementById('resource-search-btn').addEventListener('click', this.handleResourceSearch.bind(this));
        document.getElementById('resource-search').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.handleResourceSearch();
            }
        });
        document.getElementById('apply-filters-btn').addEventListener('click', this.applyFilters.bind(this));
        document.getElementById('clear-filters-btn').addEventListener('click', this.clearFilters.bind(this));

        // Load courses for filter dropdown
        this.loadCoursesForFilter();
        
        // Load resources
        this.loadResources();
    },

    /**
     * Load courses for filter dropdown
     */
    loadCoursesForFilter: async function() {
        try {
            const courses = await API.course.getAll();
            const courseFilter = document.getElementById('course-filter');
            
            if (courses && courses.data) {
                courses.data.forEach(course => {
                    const option = document.createElement('option');
                    option.value = course.id;
                    option.textContent = `${course.courseCode} - ${course.title}`;
                    courseFilter.appendChild(option);
                });
            }
        } catch (error) {
            console.error('Error loading courses for filter:', error);
        }
    },

    /**
     * Load resources with pagination
     */
    loadResources: async function() {
        try {
            Utils.showLoading();
            
            let response;
            if (Object.keys(this.filters).length > 0) {
                response = await API.resource.getFiltered(this.filters, this.currentPage, this.pageSize);
            } else {
                response = await API.resource.getPaginated(this.currentPage, this.pageSize);
            }
            
            Utils.hideLoading();
            
            // Check if response exists
            if (!response) {
                console.error("No response received from API");
                this.renderEmptyResources("Error: No response received from API");
                return;
            }
            
            // Update pagination info
            this.totalPages = response.totalPages || 0;
            
            // Render resources - handle both direct content array or nested content
            const resources = response.content || (response.data && response.data.content) || [];
            this.renderResources(resources);
            
            // Render pagination
            this.renderPagination();
        } catch (error) {
            Utils.hideLoading();
            console.error('Error loading resources:', error);
            this.renderEmptyResources(`Failed to load resources: ${error.message || 'Unknown error'}`);
        }
    },

    /**
     * Render empty resources message
     * @param {string} message - Message to display
     */
    renderEmptyResources: function(message) {
        const tableBody = document.getElementById('resources-table-body');
        tableBody.innerHTML = `
            <tr>
                <td colspan="5" class="px-6 py-4 text-center text-gray-500">${message || 'No resources found'}</td>
            </tr>
        `;
        
        // Hide pagination
        document.getElementById('resource-pagination').innerHTML = '';
    },

    /**
     * Render resources in table
     * @param {Array} resources - Array of resource objects
     */
    renderResources: function(resources) {
        const tableBody = document.getElementById('resources-table-body');
        tableBody.innerHTML = '';
        
        if (!resources || resources.length === 0) {
            this.renderEmptyResources();
            return;
        }
        
        resources.forEach(resource => {
            const row = document.createElement('tr');
            row.className = 'hover:bg-gray-50';
            row.innerHTML = `
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${resource.id}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${resource.title}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${resource.resourceType}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${resource.courseCode || 'N/A'}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    <button class="text-primary hover:text-primary-dark mr-2 edit-resource" data-id="${resource.id}">Edit</button>
                    <button class="text-red-600 hover:text-red-800 delete-resource" data-id="${resource.id}">Delete</button>
                    <button class="text-blue-600 hover:text-blue-800 ml-2 view-resource" data-id="${resource.id}">View</button>
                </td>
            `;
            tableBody.appendChild(row);
            
            // Add event listeners
            row.querySelector('.edit-resource').addEventListener('click', () => this.handleEditResource(resource.id));
            row.querySelector('.delete-resource').addEventListener('click', () => this.handleDeleteResource(resource.id));
            row.querySelector('.view-resource').addEventListener('click', () => this.handleViewResource(resource.id));
        });
    },

    /**
     * Render pagination controls
     */
    renderPagination: function() {
        const paginationContainer = document.getElementById('resource-pagination');
        paginationContainer.innerHTML = '';
        
        if (this.totalPages <= 1) {
            return;
        }
        
        const pagination = Utils.createPagination(this.currentPage, this.totalPages, (page) => {
            this.currentPage = page;
            this.loadResources();
        });
        
        paginationContainer.appendChild(pagination);
    },

    /**
     * Show create resource form
     */
    showCreateResourceForm: async function() {
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
            
            Swal.fire({
                title: 'Create Resource',
                html: `
                    <form id="create-resource-form" class="space-y-4">
                        <div>
                            <label for="resource-title" class="block text-sm font-medium text-gray-700 text-left">Title</label>
                            <input type="text" id="resource-title" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                        </div>
                        <div>
                            <label for="resource-description" class="block text-sm font-medium text-gray-700 text-left">Description</label>
                            <textarea id="resource-description" rows="3" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border"></textarea>
                        </div>
                        <div>
                            <label for="resource-type" class="block text-sm font-medium text-gray-700 text-left">Resource Type</label>
                            <select id="resource-type" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                                <option value="BOOK">Book</option>
                                <option value="ARTICLE">Article</option>
                                <option value="VIDEO">Video</option>
                                <option value="OTHER">Other</option>
                            </select>
                        </div>
                        <div>
                            <label for="resource-course" class="block text-sm font-medium text-gray-700 text-left">Course</label>
                            <select id="resource-course" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                                <option value="">Select Course</option>
                                ${courseOptions}
                            </select>
                        </div>
                        <div>
                            <label for="resource-url" class="block text-sm font-medium text-gray-700 text-left">URL</label>
                            <input type="text" id="resource-url" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                        </div>
                        <div id="book-specific-fields" class="hidden">
                            <div>
                                <label for="resource-author" class="block text-sm font-medium text-gray-700 text-left">Author</label>
                                <input type="text" id="resource-author" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                            </div>
                            <div>
                                <label for="resource-publisher" class="block text-sm font-medium text-gray-700 text-left">Publisher</label>
                                <input type="text" id="resource-publisher" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                            </div>
                            <div>
                                <label for="resource-isbn" class="block text-sm font-medium text-gray-700 text-left">ISBN</label>
                                <input type="text" id="resource-isbn" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                            </div>
                        </div>
                    </form>
                `,
                showCancelButton: true,
                confirmButtonText: 'Create',
                confirmButtonColor: '#3B82F6',
                cancelButtonText: 'Cancel',
                focusConfirm: false,
                didOpen: () => {
                    // Show/hide book-specific fields based on resource type
                    const resourceTypeSelect = document.getElementById('resource-type');
                    const bookSpecificFields = document.getElementById('book-specific-fields');
                    
                    resourceTypeSelect.addEventListener('change', () => {
                        if (resourceTypeSelect.value === 'BOOK') {
                            bookSpecificFields.classList.remove('hidden');
                        } else {
                            bookSpecificFields.classList.add('hidden');
                        }
                    });
                },
                preConfirm: () => {
                    // Validate form
                    const title = document.getElementById('resource-title').value;
                    const description = document.getElementById('resource-description').value;
                    const resourceType = document.getElementById('resource-type').value;
                    const courseId = document.getElementById('resource-course').value;
                    const url = document.getElementById('resource-url').value;
                    
                    if (!title) {
                        Swal.showValidationMessage('Title is required');
                        return false;
                    }
                    
                    if (!description) {
                        Swal.showValidationMessage('Description is required');
                        return false;
                    }
                    
                    if (!courseId) {
                        Swal.showValidationMessage('Course is required');
                        return false;
                    }
                    
                    // Create resource object
                    const resource = {
                        title,
                        description,
                        resourceType,
                        courseId: parseInt(courseId),
                        url
                    };
                    
                    // Add book-specific fields if resource type is BOOK
                    if (resourceType === 'BOOK') {
                        resource.author = document.getElementById('resource-author').value;
                        resource.publisher = document.getElementById('resource-publisher').value;
                        resource.isbn = document.getElementById('resource-isbn').value;
                    }
                    
                    return resource;
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    this.createResource(result.value);
                }
            });
        } catch (error) {
            Utils.hideLoading();
            console.error('Error preparing create resource form:', error);
            Utils.showErrorToast('Failed to prepare create resource form');
        }
    },

    /**
     * Create a new resource
     * @param {Object} resource - Resource data
     */
    createResource: async function(resource) {
        try {
            Utils.showLoading();
            
            const response = await API.resource.create(resource);
            
            Utils.hideLoading();
            
            if (response && response.success) {
                Utils.showSuccessToast('Resource created successfully');
                this.loadResources();
            } else {
                Utils.showErrorToast(response.message || 'Failed to create resource');
            }
        } catch (error) {
            Utils.hideLoading();
            console.error('Error creating resource:', error);
            Utils.showErrorToast('Failed to create resource');
        }
    },

    /**
     * Handle edit resource button click
     * @param {number} resourceId - Resource ID
     */
    handleEditResource: async function(resourceId) {
        try {
            Utils.showLoading();
            
            // Load resource details
            const resourceResponse = await API.resource.getById(resourceId);
            
            // Load courses for dropdown
            const coursesResponse = await API.course.getAll();
            
            Utils.hideLoading();
            
            if (!resourceResponse || !resourceResponse.success) {
                Utils.showErrorToast('Failed to load resource details');
                return;
            }
            
            const resource = resourceResponse.data;
            this.currentResource = resource;
            
            let courseOptions = '';
            if (coursesResponse && coursesResponse.data) {
                coursesResponse.data.forEach(course => {
                    const selected = resource.course && resource.course.id === course.id ? 'selected' : '';
                    courseOptions += `<option value="${course.id}" ${selected}>${course.courseCode} - ${course.title}</option>`;
                });
            }
            
            Swal.fire({
                title: 'Edit Resource',
                html: `
                    <form id="edit-resource-form" class="space-y-4">
                        <div>
                            <label for="edit-resource-title" class="block text-sm font-medium text-gray-700 text-left">Title</label>
                            <input type="text" id="edit-resource-title" value="${resource.title}" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                        </div>
                        <div>
                            <label for="edit-resource-description" class="block text-sm font-medium text-gray-700 text-left">Description</label>
                            <textarea id="edit-resource-description" rows="3" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">${resource.description || ''}</textarea>
                        </div>
                        <div>
                            <label for="edit-resource-course" class="block text-sm font-medium text-gray-700 text-left">Course</label>
                            <select id="edit-resource-course" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                                <option value="">Select Course</option>
                                ${courseOptions}
                            </select>
                        </div>
                        <div>
                            <label for="edit-resource-url" class="block text-sm font-medium text-gray-700 text-left">URL</label>
                            <input type="text" id="edit-resource-url" value="${resource.url || ''}" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                        </div>
                        ${resource.resourceType === 'BOOK' ? `
                        <div id="edit-book-specific-fields">
                            <div>
                                <label for="edit-resource-author" class="block text-sm font-medium text-gray-700 text-left">Author</label>
                                <input type="text" id="edit-resource-author" value="${resource.author || ''}" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                            </div>
                            <div>
                                <label for="edit-resource-publisher" class="block text-sm font-medium text-gray-700 text-left">Publisher</label>
                                <input type="text" id="edit-resource-publisher" value="${resource.publisher || ''}" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                            </div>
                            <div>
                                <label for="edit-resource-isbn" class="block text-sm font-medium text-gray-700 text-left">ISBN</label>
                                <input type="text" id="edit-resource-isbn" value="${resource.isbn || ''}" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring focus:ring-primary focus:ring-opacity-50 p-2 border">
                            </div>
                        </div>
                        ` : ''}
                    </form>
                `,
                showCancelButton: true,
                confirmButtonText: 'Update',
                confirmButtonColor: '#3B82F6',
                cancelButtonText: 'Cancel',
                focusConfirm: false,
                preConfirm: () => {
                    // Validate form
                    const title = document.getElementById('edit-resource-title').value;
                    const description = document.getElementById('edit-resource-description').value;
                    const courseId = document.getElementById('edit-resource-course').value;
                    const url = document.getElementById('edit-resource-url').value;
                    
                    if (!title) {
                        Swal.showValidationMessage('Title is required');
                        return false;
                    }
                    
                    if (!description) {
                        Swal.showValidationMessage('Description is required');
                        return false;
                    }
                    
                    if (!courseId) {
                        Swal.showValidationMessage('Course is required');
                        return false;
                    }
                    
                    // Create update object
                    const updatedResource = {
                        title,
                        description,
                        courseId: parseInt(courseId),
                        url
                    };
                    
                    // Add book-specific fields if resource type is BOOK
                    if (resource.resourceType === 'BOOK') {
                        updatedResource.author = document.getElementById('edit-resource-author').value;
                        updatedResource.publisher = document.getElementById('edit-resource-publisher').value;
                        updatedResource.isbn = document.getElementById('edit-resource-isbn').value;
                    }
                    
                    return updatedResource;
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    this.updateResource(resourceId, result.value);
                }
            });
        } catch (error) {
            Utils.hideLoading();
            console.error('Error preparing edit resource form:', error);
            Utils.showErrorToast('Failed to prepare edit resource form');
        }
    },

    /**
     * Update a resource
     * @param {number} resourceId - Resource ID
     * @param {Object} updatedResource - Updated resource data
     */
    updateResource: async function(resourceId, updatedResource) {
        try {
            Utils.showLoading();
            
            const response = await API.resource.update(resourceId, updatedResource);
            
            Utils.hideLoading();
            
            if (response && response.success) {
                Utils.showSuccessToast('Resource updated successfully');
                this.loadResources();
            } else {
                Utils.showErrorToast(response.message || 'Failed to update resource');
            }
        } catch (error) {
            Utils.hideLoading();
            console.error('Error updating resource:', error);
            Utils.showErrorToast('Failed to update resource');
        }
    },

    /**
     * Handle delete resource button click
     * @param {number} resourceId - Resource ID
     */
    handleDeleteResource: function(resourceId) {
        Swal.fire({
            title: 'Delete Resource',
            text: 'Are you sure you want to delete this resource? This action cannot be undone.',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Delete',
            confirmButtonColor: '#EF4444',
            cancelButtonText: 'Cancel',
            focusCancel: true
        }).then((result) => {
            if (result.isConfirmed) {
                this.deleteResource(resourceId);
            }
        });
    },

    /**
     * Delete a resource
     * @param {number} resourceId - Resource ID
     */
    deleteResource: async function(resourceId) {
        try {
            Utils.showLoading();
            
            const response = await API.resource.delete(resourceId);
            
            Utils.hideLoading();
            
            if (response && response.success) {
                Utils.showSuccessToast('Resource deleted successfully');
                this.loadResources();
            } else {
                Utils.showErrorToast(response.message || 'Failed to delete resource');
            }
        } catch (error) {
            Utils.hideLoading();
            console.error('Error deleting resource:', error);
            Utils.showErrorToast('Failed to delete resource');
        }
    },

    /**
     * Handle view resource button click
     * @param {number} resourceId - Resource ID
     */
    handleViewResource: async function(resourceId) {
        try {
            Utils.showLoading();
            
            const response = await API.resource.getById(resourceId);
            
            Utils.hideLoading();
            
            if (!response || !response.success) {
                Utils.showErrorToast('Failed to load resource details');
                return;
            }
            
            const resource = response.data;
            
            let resourceDetails = `
                <div class="text-left">
                    <p class="mb-2"><strong>Title:</strong> ${resource.title}</p>
                    <p class="mb-2"><strong>Description:</strong> ${resource.description || 'N/A'}</p>
                    <p class="mb-2"><strong>Type:</strong> ${resource.resourceType}</p>
                    <p class="mb-2"><strong>Course:</strong> ${resource.course ? `${resource.course.courseCode} - ${resource.course.title}` : 'N/A'}</p>
                    <p class="mb-2"><strong>URL:</strong> ${resource.url ? `<a href="${resource.url}" target="_blank" class="text-blue-600 hover:underline">${resource.url}</a>` : 'N/A'}</p>
            `;
            
            // Add book-specific fields if resource type is BOOK
            if (resource.resourceType === 'BOOK') {
                resourceDetails += `
                    <p class="mb-2"><strong>Author:</strong> ${resource.author || 'N/A'}</p>
                    <p class="mb-2"><strong>Publisher:</strong> ${resource.publisher || 'N/A'}</p>
                    <p class="mb-2"><strong>ISBN:</strong> ${resource.isbn || 'N/A'}</p>
                `;
            }
            
            resourceDetails += '</div>';
            
            Swal.fire({
                title: 'Resource Details',
                html: resourceDetails,
                confirmButtonText: 'Close',
                confirmButtonColor: '#3B82F6'
            });
        } catch (error) {
            Utils.hideLoading();
            console.error('Error loading resource details:', error);
            Utils.showErrorToast('Failed to load resource details');
        }
    },

    /**
     * Handle resource search
     */
    handleResourceSearch: function() {
        const searchTerm = document.getElementById('resource-search').value.trim();
        this.filters.search = searchTerm || null;
        this.currentPage = 0;
        this.loadResources();
    },

    /**
     * Apply filters
     */
    applyFilters: function() {
        const resourceType = document.getElementById('resource-type-filter').value;
        const courseId = document.getElementById('course-filter').value;
        
        this.filters = {};
        
        if (resourceType) {
            this.filters.resourceType = resourceType;
        }
        
        if (courseId) {
            this.filters.courseId = parseInt(courseId);
        }
        
        const searchTerm = document.getElementById('resource-search').value.trim();
        if (searchTerm) {
            this.filters.search = searchTerm;
        }
        
        this.currentPage = 0;
        this.loadResources();
    },

    /**
     * Clear filters
     */
    clearFilters: function() {
        document.getElementById('resource-type-filter').value = '';
        document.getElementById('course-filter').value = '';
        document.getElementById('resource-search').value = '';
        
        this.filters = {};
        this.currentPage = 0;
        this.loadResources();
    }
};

// Initialize Resource module when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    Resource.init();
});
