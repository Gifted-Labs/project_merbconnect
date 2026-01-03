/**
 * Main application module for the Academic Management System
 */

const App = {
    sidebarCollapsed: false,

    /**
     * Initialize application
     */
    init: function() {
        this.bindEvents();
        this.setupNavigation();
        this.initializeSidebar();
    },

    /**
     * Initialize sidebar functionality
     */
    initializeSidebar: function() {
        const sidebar = document.getElementById('sidebar');
        const sidebarToggle = document.getElementById('sidebar-toggle');
        const mobileMenuBtn = document.getElementById('mobile-menu-btn');
        const sidebarOverlay = document.getElementById('sidebar-overlay');
        const mainContent = document.getElementById('main-content');

        // Desktop sidebar toggle
        if (sidebarToggle) {
            sidebarToggle.addEventListener('click', () => {
                this.toggleSidebar();
            });
        }

        // Mobile menu toggle
        if (mobileMenuBtn) {
            mobileMenuBtn.addEventListener('click', () => {
                this.showMobileSidebar();
            });
        }

        // Close mobile sidebar when clicking overlay
        if (sidebarOverlay) {
            sidebarOverlay.addEventListener('click', () => {
                this.hideMobileSidebar();
            });
        }

        // Handle window resize
        window.addEventListener('resize', () => {
            if (window.innerWidth >= 1024) {
                this.hideMobileSidebar();
                this.updateMainContentMargin();
            }
        });

        // Initialize sidebar state
        this.updateMainContentMargin();

        // Ensure sidebar is properly positioned on desktop
        if (window.innerWidth >= 1024) {
            const sidebar = document.getElementById('sidebar');
            if (sidebar && !sidebar.classList.contains('hidden')) {
                sidebar.classList.add('show');
            }
        }
    },

    /**
     * Toggle sidebar collapse state
     */
    toggleSidebar: function() {
        const sidebar = document.getElementById('sidebar');
        const toggleIcon = document.querySelector('#sidebar-toggle i');

        this.sidebarCollapsed = !this.sidebarCollapsed;

        if (this.sidebarCollapsed) {
            sidebar.classList.add('collapsed');
            toggleIcon.className = 'fas fa-chevron-right text-gray-500';
        } else {
            sidebar.classList.remove('collapsed');
            toggleIcon.className = 'fas fa-chevron-left text-gray-500';
        }

        this.updateMainContentMargin();
    },

    /**
     * Show mobile sidebar
     */
    showMobileSidebar: function() {
        const sidebar = document.getElementById('sidebar');
        const overlay = document.getElementById('sidebar-overlay');

        sidebar.classList.add('show');
        overlay.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
    },

    /**
     * Hide mobile sidebar
     */
    hideMobileSidebar: function() {
        const sidebar = document.getElementById('sidebar');
        const overlay = document.getElementById('sidebar-overlay');

        sidebar.classList.remove('show');
        overlay.classList.add('hidden');
        document.body.style.overflow = '';
    },

    /**
     * Update main content margin based on sidebar state
     */
    updateMainContentMargin: function() {
        const mainWrapper = document.getElementById('main-wrapper');

        if (window.innerWidth >= 1024) {
            if (this.sidebarCollapsed) {
                mainWrapper.classList.remove('sidebar-open');
                mainWrapper.classList.add('sidebar-collapsed');
            } else {
                mainWrapper.classList.remove('sidebar-collapsed');
                mainWrapper.classList.add('sidebar-open');
            }
        } else {
            mainWrapper.classList.remove('sidebar-open', 'sidebar-collapsed');
        }
    },

    /**
     * Bind global event listeners
     */
    bindEvents: function() {
        // Handle page navigation
        const navLinks = document.querySelectorAll('[data-page]');
        navLinks.forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const page = link.getAttribute('data-page');
                this.navigateTo(page);

                // Close mobile sidebar after navigation
                this.hideMobileSidebar();
            });
        });

        // Note: Logout button is handled by auth.js
    },

    /**
     * Setup navigation based on authentication state
     */
    setupNavigation: function() {
        console.log('App.setupNavigation called');

        // Check if user is authenticated
        if (API.isAuthenticated()) {
            console.log('User is authenticated, setting up UI');

            // Show user info
            const user = JSON.parse(localStorage.getItem('user') || '{}');
            const userNameElement = document.getElementById('user-name');
            if (userNameElement) {
                if (user.email) {
                    userNameElement.textContent = user.email;
                } else if (user.firstName && user.lastName) {
                    userNameElement.textContent = `${user.firstName} ${user.lastName}`;
                } else {
                    userNameElement.textContent = 'User';
                }
                console.log('User name set:', userNameElement.textContent);
            }

            // Hide auth container
            const authContainer = document.getElementById('auth-container');
            if (authContainer) {
                authContainer.classList.add('hidden');
            }

            // Show navigation elements
            const sidebar = document.getElementById('sidebar');
            const mobileTopbar = document.getElementById('mobile-topbar');

            if (sidebar) {
                sidebar.classList.remove('hidden');
                // Show sidebar on desktop by default
                if (window.innerWidth >= 1024) {
                    sidebar.classList.add('show');
                }
                console.log('Sidebar shown');
            }
            if (mobileTopbar) {
                mobileTopbar.classList.remove('hidden');
                console.log('Mobile topbar shown');
            }

            // Update main content margin
            this.updateMainContentMargin();

            // Load dashboard data
            this.loadDashboardData();

            // Navigate to dashboard by default
            this.navigateTo('dashboard');
            console.log('Navigation setup complete');
        } else {
            console.log('User not authenticated, showing login');

            // Hide navigation elements
            const sidebar = document.getElementById('sidebar');
            const mobileTopbar = document.getElementById('mobile-topbar');

            if (sidebar) {
                sidebar.classList.add('hidden');
                sidebar.classList.remove('show');
            }
            if (mobileTopbar) mobileTopbar.classList.add('hidden');

            // Show auth container
            const authContainer = document.getElementById('auth-container');
            if (authContainer) authContainer.classList.remove('hidden');
        }
    },

    /**
     * Navigate to a specific page
     * @param {string} page - Page identifier
     */
    navigateTo: function(page) {
        // Hide all containers
        const containers = [
            'dashboard-container',
            'college-container',
            'faculty-container',
            'department-container',
            'program-container',
            'course-container',
            'resource-container',
            'quiz-container',
            'quiz-taking-container',
            'quiz-results-container',
            'auth-container'
        ];

        containers.forEach(container => {
            const element = document.getElementById(container);
            if (element) element.classList.add('hidden');
        });

        // Show selected container
        const selectedContainer = document.getElementById(`${page}-container`);
        if (selectedContainer) selectedContainer.classList.remove('hidden');

        // Update active navigation link
        const navLinks = document.querySelectorAll('.nav-item');
        navLinks.forEach(link => {
            if (link.getAttribute('data-page') === page) {
                link.classList.add('active');
            } else {
                link.classList.remove('active');
            }
        });

        // Load page-specific data
        this.loadPageData(page);
    },

    /**
     * Load page-specific data
     * @param {string} page - Page identifier
     */
    loadPageData: function(page) {
        switch (page) {
            case 'dashboard':
                // Dashboard data is loaded in setupNavigation()
                break;
            case 'college':
                if (window.College) College.loadColleges();
                break;
            case 'faculty':
                if (window.Faculty) Faculty.loadFaculties();
                break;
            case 'department':
                if (window.Department) Department.loadDepartments();
                break;
            case 'program':
                if (window.Program) Program.loadPrograms();
                break;
            case 'course':
                if (window.Course) Course.loadCourses();
                break;
            case 'resource':
                if (window.Resource) Resource.loadResources();
                break;
            case 'quiz':
                if (window.Quiz) Quiz.showQuizSection();
                break;
        }
    },

    /**
     * Load dashboard data
     */
    loadDashboardData: async function() {
        try {
            Utils.showLoading();

            // Simple sequential loading of each entity type
            const collegesResponse = await API.college.getAll();
            const facultiesResponse = await API.faculty.getAll();
            const departmentsResponse = await API.department.getAll();
            const programsResponse = await API.program.getAll();
            const coursesResponse = await API.course.getAll();
            const resourcesResponse = await API.resource.getAll();

            console.log("Dashboard data loaded:", {
                colleges: collegesResponse,
                faculties: facultiesResponse,
                departments: departmentsResponse,
                programs: programsResponse,
                courses: coursesResponse,
                resources: resourcesResponse
            });

            // Update dashboard counts
            document.getElementById('college-count').textContent =
                collegesResponse && collegesResponse.data ? collegesResponse.data.length : 0;

            document.getElementById('faculty-count').textContent =
                facultiesResponse && facultiesResponse.data ? facultiesResponse.data.length : 0;

            document.getElementById('department-count').textContent =
                departmentsResponse && departmentsResponse.data ? departmentsResponse.data.length : 0;

            document.getElementById('program-count').textContent =
                programsResponse && programsResponse.data ? programsResponse.data.length : 0;

            // Update course count
            const courseCountElement = document.getElementById('course-count');
            if (courseCountElement) {
                courseCountElement.textContent =
                    coursesResponse && coursesResponse.data ? coursesResponse.data.length : 0;
            }

            // Update resource count
            const resourceCountElement = document.getElementById('resource-count');
            if (resourceCountElement) {
                resourceCountElement.textContent =
                    resourcesResponse && resourcesResponse.data ? resourcesResponse.data.length : 0;
            }

            Utils.hideLoading();
        } catch (error) {
            Utils.hideLoading();
            console.error('Error loading dashboard data:', error);
        }
    }
};

// Initialize application when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    App.init();
});

// Export App object
window.App = App;
