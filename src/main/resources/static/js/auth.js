/**
 * Authentication module for the Academic Management System
 */

const Auth = {
    /**
     * Initialize authentication module
     */
    init: function() {
        this.bindEvents();
        this.checkAuth();
    },

    /**
     * Bind event listeners
     */
    bindEvents: function() {
        // Login form submission
        const loginForm = document.getElementById('login-form');
        if (loginForm) {
            const loginBtn = document.getElementById('login-btn');
            loginBtn.addEventListener('click', this.handleLogin.bind(this));
        }

        // Register form submission
        const registerForm = document.getElementById('register-form');
        if (registerForm) {
            const registerBtn = document.getElementById('register-btn');
            registerBtn.addEventListener('click', this.handleRegister.bind(this));
        }

        // Show register form
        const showRegisterLink = document.getElementById('show-register');
        if (showRegisterLink) {
            showRegisterLink.addEventListener('click', function(e) {
                e.preventDefault();
                document.getElementById('login-form').classList.add('hidden');
                document.getElementById('register-form').classList.remove('hidden');
            });
        }

        // Show login form
        const showLoginLink = document.getElementById('show-login');
        if (showLoginLink) {
            showLoginLink.addEventListener('click', function(e) {
                e.preventDefault();
                document.getElementById('register-form').classList.add('hidden');
                document.getElementById('login-form').classList.remove('hidden');
            });
        }

        // Logout button
        const logoutBtn = document.getElementById('logout-btn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', this.handleLogout.bind(this));
        }
    },

    /**
     * Check if user is authenticated
     */
    checkAuth: function() {
        if (API.isAuthenticated()) {
            this.showAuthenticatedUI();
        } else {
            this.showUnauthenticatedUI();
        }
    },

    /**
     * Show authenticated UI
     */
    showAuthenticatedUI: function() {
        console.log('showAuthenticatedUI called');

        // Hide auth container first
        const authContainer = document.getElementById('auth-container');
        if (authContainer) {
            authContainer.classList.add('hidden');
        }

        // Use the App's setupNavigation method if available
        if (window.App && window.App.setupNavigation) {
            console.log('Using App.setupNavigation');
            window.App.setupNavigation();
        } else {
            console.log('Using fallback UI handling');

            // Show sidebar elements
            const sidebar = document.getElementById('sidebar');
            const mobileTopbar = document.getElementById('mobile-topbar');

            if (sidebar) {
                sidebar.classList.remove('hidden');
                if (window.innerWidth >= 1024) {
                    sidebar.classList.add('show');
                }
                console.log('Sidebar shown');
            }
            if (mobileTopbar) {
                mobileTopbar.classList.remove('hidden');
                console.log('Mobile topbar shown');
            }

            // Show dashboard
            const dashboardContainer = document.getElementById('dashboard-container');
            if (dashboardContainer) {
                dashboardContainer.classList.remove('hidden');
                console.log('Dashboard container shown');
            }

            // Update main content margin
            if (window.App && window.App.updateMainContentMargin) {
                window.App.updateMainContentMargin();
            }

            // Load dashboard data
            if (window.App && window.App.loadDashboardData) {
                window.App.loadDashboardData();
            } else {
                this.loadDashboardData();
            }
        }
    },

    /**
     * Show unauthenticated UI
     */
    showUnauthenticatedUI: function() {
        // Show auth container
        document.getElementById('auth-container').classList.remove('hidden');

        // Hide sidebar elements
        const sidebar = document.getElementById('sidebar');
        const mobileTopbar = document.getElementById('mobile-topbar');

        if (sidebar) {
            sidebar.classList.add('hidden');
            sidebar.classList.remove('show');
        }
        if (mobileTopbar) mobileTopbar.classList.add('hidden');

        // Hide all content containers
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
            'quiz-results-container'
        ];

        containers.forEach(container => {
            const element = document.getElementById(container);
            if (element) element.classList.add('hidden');
        });

        // Reset main content margin
        const mainWrapper = document.getElementById('main-wrapper');
        if (mainWrapper) {
            mainWrapper.classList.remove('sidebar-open', 'sidebar-collapsed');
        }
    },

    /**
     * Handle login form submission
     * @param {Event} e - Form submission event
     */
    handleLogin: async function(e) {
        e.preventDefault();

        const email = document.getElementById('login-email').value;
        const password = document.getElementById('login-password').value;

        // Validate inputs
        if (!email || !password) {
            Utils.showError('Please enter both email and password');
            return;
        }

        if (!Utils.validateEmail(email)) {
            Utils.showError('Please enter a valid email address');
            return;
        }

        try {
            Utils.showLoading();
            console.log('Attempting login with:', { email });
            const response = await API.auth.login(email, password);
            console.log('Login response:', response);

            // Ensure token is properly set
            if (response.token) {
                API.setToken(response.token);
                console.log('Token set successfully:', response.token);
            } else {
                console.warn('No token received in login response');
            }

            Utils.hideLoading();

            // Store user info
            const userInfo = {
                id: response.id || response.userId,
                email: response.username || response.email,
                roles: response.roles || []
            };
            localStorage.setItem('user', JSON.stringify(userInfo));
            console.log('User info stored:', userInfo);

            // Update UI
            this.showAuthenticatedUI();
            Utils.showSuccess('Login successful');
        } catch (error) {
            Utils.hideLoading();
            console.error('Login error:', error);
            Utils.showError(error.message || 'Login failed. Please check your credentials.');
        }
    },

    /**
     * Handle register form submission
     * @param {Event} e - Form submission event
     */
    handleRegister: async function(e) {
        e.preventDefault();

        const firstName = document.getElementById('register-firstname').value;
        const lastName = document.getElementById('register-lastname').value;
        const email = document.getElementById('register-email').value;
        const phoneNumber = document.getElementById('register-phone').value;
        const password = document.getElementById('register-password').value;

        // Validate inputs
        if (!firstName || !lastName || !email || !phoneNumber || !password) {
            Utils.showError('Please fill in all fields');
            return;
        }

        if (!Utils.validateEmail(email)) {
            Utils.showError('Please enter a valid email address');
            return;
        }

        if (password.length < 6) {
            Utils.showError('Password must be at least 6 characters long');
            return;
        }

        try {
            Utils.showLoading();
            await API.auth.register({
                firstName,
                lastName,
                email,
                phoneNumber,
                password
            });
            Utils.hideLoading();

            // Show login form
            document.getElementById('register-form').classList.add('hidden');
            document.getElementById('login-form').classList.remove('hidden');

            Utils.showSuccess('Registration successful. Please check your email for verification.');
        } catch (error) {
            Utils.hideLoading();
            Utils.showError(error.message || 'Registration failed. Please try again.');
        }
    },

    /**
     * Handle logout
     */
    handleLogout: function() {
        API.auth.logout();
        localStorage.removeItem('user');
        localStorage.removeItem('token');
        this.showUnauthenticatedUI();
        Utils.showSuccessToast('Logout successful');
    },

    /**
     * Load dashboard data
     */
    loadDashboardData: async function() {
        try {
            Utils.showLoading();

            // Load counts
            const [colleges, faculties, departments, programs, courses] = await Promise.all([
                API.college.getAll(),
                API.faculty.getAll(),
                API.department.getAll(),
                API.program.getAll(),
                API.course.getAll()
            ]);

            console.log("Dashboard data loaded:", { colleges, faculties, departments, programs, courses });

            // Update dashboard counts - handle different response formats
            document.getElementById('college-count').textContent =
                colleges.data ? (Array.isArray(colleges.data) ? colleges.data.length : 0) : 0;

            document.getElementById('faculty-count').textContent =
                faculties.data ? (Array.isArray(faculties.data) ? faculties.data.length : 0) : 0;

            document.getElementById('department-count').textContent =
                departments.data ? (Array.isArray(departments.data) ? departments.data.length : 0) : 0;

            document.getElementById('program-count').textContent =
                programs.data ? (Array.isArray(programs.data) ? programs.data.length : 0) : 0;

            // Add course count to dashboard
            const courseCountElement = document.getElementById('course-count');
            if (courseCountElement) {
                courseCountElement.textContent =
                    courses.data ? (Array.isArray(courses.data) ? courses.data.length : 0) : 0;
            }

            Utils.hideLoading();
        } catch (error) {
            Utils.hideLoading();
            console.error('Error loading dashboard data:', error);
        }
    }
};

// Initialize authentication module when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Wait a bit to ensure App is loaded first
    setTimeout(() => {
        Auth.init();
    }, 100);
});

// Export Auth object
window.Auth = Auth;
