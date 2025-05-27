/**
 * Main application module for the Academic Management System
 */

const App = {
    /**
     * Initialize application
     */
    init: function() {
        this.bindEvents();
        this.setupNavigation();
    },

    /**
     * Bind global event listeners
     */
    bindEvents: function() {
        // Mobile menu toggle
        const mobileMenuBtn = document.getElementById('mobile-menu-btn');
        const mobileMenu = document.getElementById('mobile-menu');
        
        if (mobileMenuBtn && mobileMenu) {
            mobileMenuBtn.addEventListener('click', function() {
                mobileMenu.classList.toggle('hidden');
            });
        }
        
        // Close mobile menu when clicking outside
        document.addEventListener('click', function(e) {
            if (mobileMenu && !mobileMenu.classList.contains('hidden') && 
                !mobileMenuBtn.contains(e.target) && !mobileMenu.contains(e.target)) {
                mobileMenu.classList.add('hidden');
            }
        });
        
        // Handle page navigation
        const navLinks = document.querySelectorAll('[data-page]');
        navLinks.forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const page = link.getAttribute('data-page');
                this.navigateTo(page);
                
                // Close mobile menu after navigation
                if (mobileMenu) {
                    mobileMenu.classList.add('hidden');
                }
            });
        });
    },

    /**
     * Setup navigation based on authentication state
     */
    setupNavigation: function() {
        // Check if user is authenticated
        if (API.isAuthenticated()) {
            // Show user info
            const user = JSON.parse(localStorage.getItem('user') || '{}');
            const userNameElement = document.getElementById('user-name');
            if (userNameElement && user.email) {
                userNameElement.textContent = user.email;
            }
            
            // Show navigation
            const mainNav = document.getElementById('main-nav');
            const mobileMenuBtn = document.getElementById('mobile-menu-btn');
            
            if (mainNav) mainNav.classList.remove('hidden');
            if (mobileMenuBtn) mobileMenuBtn.classList.remove('hidden');
            
            // Navigate to dashboard by default
            this.navigateTo('dashboard');
        } else {
            // Hide navigation
            const mainNav = document.getElementById('main-nav');
            const mobileMenuBtn = document.getElementById('mobile-menu-btn');
            
            if (mainNav) mainNav.classList.add('hidden');
            if (mobileMenuBtn) mobileMenuBtn.classList.add('hidden');
            
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
            'auth-container'
        ];
        
        containers.forEach(container => {
            const element = document.getElementById(container);
            if (element) element.classList.add('hidden');
        });
        
        // Show selected container
        const selectedContainer = document.getElementById(`${page}-container`);
        if (selectedContainer) selectedContainer.classList.remove('hidden');
        
        // Highlight active navigation link
        const navLinks = document.querySelectorAll('[data-page]');
        navLinks.forEach(link => {
            if (link.getAttribute('data-page') === page) {
                link.classList.add('text-white', 'font-bold');
                link.classList.remove('hover:text-blue-200');
            } else {
                link.classList.remove('text-white', 'font-bold');
                link.classList.add('hover:text-blue-200');
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
                // Dashboard data is loaded in loadInitialData()
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
        }
    },

    /**
     * Load dashboard data
     */
    loadDashboardData: async function() {
        try {
            Utils.showLoading();
            
            // Load counts
            const [colleges, faculties, departments, programs] = await Promise.all([
                API.college.getAll(),
                API.faculty.getAll(),
                API.department.getAll(),
                API.program.getAll()
            ]);
            
            // Update dashboard counts
            document.getElementById('college-count').textContent = colleges.length;
            document.getElementById('faculty-count').textContent = faculties.length;
            document.getElementById('department-count').textContent = departments.length;
            document.getElementById('program-count').textContent = programs.length;
            
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
