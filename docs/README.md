# BE-DMS Documentation Hub

## 📚 **Documentation Overview**

Welcome to the comprehensive documentation for **BE-DMS** (Backend Document Management System). This documentation is organized into several categories for easy navigation and reference.

---

## 🚀 **Quick Start**

### **For New Developers**
Start here if you're new to the project:

- **[📖 Quick Start Guide](guide/QUICK_START_GUIDE.md)** - Get up and running in minutes
- **[🔧 Project Setup](../README.md)** - Basic project information and setup

---

## 📋 **Documentation Categories**

### **1. 📖 User Guides**
**Location**: `docs/guide/`

User-friendly guides for getting started and common tasks:
- [Quick Start Guide](guide/QUICK_START_GUIDE.md) - Step-by-step setup and first run

### **2. 🔧 Technical Documentation**
**Location**: `docs/technical/`

In-depth technical documentation for developers:
- [Exception Handler Resolution](technical/EXCEPTION-HANDLER-RESOLUTION.md) - Security exception handling conflicts and solutions
- [Spring Security 6.x Exception Handling](technical/SPRING-SECURITY-6-EXCEPTION-HANDLING.md) - Modern Spring Security exception management

### **3. 🔍 Troubleshooting**
**Location**: `docs/troubleshooting/`

Common issues and their solutions:
- [JWT Token Troubleshooting](troubleshooting/JWT-TROUBLESHOOTING.md) - Debug JWT authentication issues

### **4. 📡 API Documentation**
**Location**: `docs/api/`

API references and specifications:
- [API Documentation (Markdown)](api/API_DOCUMENTATION.md) - Complete API reference
- [API Documentation (HTML)](api/documentation.html) - Interactive API documentation

### **5. 📝 Project Information**
**Location**: `docs/project/`

Project management and planning documents:
- [Rencana Kerja (HTML)](project/rencana-kerja.html) - Project work plan and implementation details

---

## 🗂 **Documentation Structure**

```
docs/
├── README.md                 # This file - main documentation hub
├── guide/                    # User guides and getting started
│   └── QUICK_START_GUIDE.md
├── technical/                # Technical implementation details
│   ├── EXCEPTION-HANDLER-RESOLUTION.md
│   └── SPRING-SECURITY-6-EXCEPTION-HANDLING.md
├── troubleshooting/          # Problem solving and debugging
│   └── JWT-TROUBLESHOOTING.md
├── api/                      # API documentation and references
│   ├── API_DOCUMENTATION.md
│   └── documentation.html
└── project/                  # Project management documents
    └── rencana-kerja.html
```

---

## 🎯 **Quick Navigation by Role**

### **For Frontend Developers**
1. [API Documentation](api/API_DOCUMENTATION.md) - Understand available endpoints
2. [JWT Troubleshooting](troubleshooting/JWT-TROUBLESHOOTING.md) - Debug authentication issues
3. [Quick Start Guide](guide/QUICK_START_GUIDE.md) - Get backend running locally

### **For Backend Developers**  
1. [Quick Start Guide](guide/QUICK_START_GUIDE.md) - Initial setup
2. [Technical Documentation](technical/) - Implementation details
3. [Exception Handling](technical/EXCEPTION-HANDLER-RESOLUTION.md) - Error handling architecture

### **For DevOps/System Administrators**
1. [Quick Start Guide](guide/QUICK_START_GUIDE.md) - Deployment setup
2. [JWT Troubleshooting](troubleshooting/JWT-TROUBLESHOOTING.md) - Security configuration
3. [Project Work Plan](project/rencana-kerja.html) - Infrastructure requirements

### **For Project Managers**
1. [Project Work Plan](project/rencana-kerja.html) - Implementation timeline and scope
2. [API Documentation](api/documentation.html) - Feature overview
3. [Technical Architecture](technical/) - System design decisions

---

## 🔄 **Documentation Maintenance**

### **Adding New Documentation**
1. Create the document in the appropriate category folder
2. Update this main README with a link to the new document
3. Follow the existing naming conventions and structure

### **Documentation Standards**
- **Markdown files**: Use `.md` extension for text-based documentation
- **HTML files**: Use `.html` extension for rich interactive documentation
- **File naming**: Use `UPPERCASE_WITH_UNDERSCORES.md` for major documents, `lowercase-with-hyphens.md` for supporting files
- **Language**: Use English for technical documentation, Indonesian for project-specific content

### **Review Process**
- Technical documentation should be reviewed by senior developers
- API documentation should be validated against actual implementation
- User guides should be tested by non-technical team members

---

## 📞 **Support and Contact**

### **For Documentation Issues**
- Check the specific document's troubleshooting section first
- Review related documents in the same category
- Check the project's main README.md for additional information

### **For Technical Support**
- Refer to [troubleshooting documentation](troubleshooting/)
- Check implementation details in [technical documentation](technical/)
- Review API specifications in [API documentation](api/)

---

## 📈 **Recent Updates**

### **Latest Changes** (August 2025)
- ✅ **Security Enhancement**: Resolved exception handler conflicts between Spring Security and Spring MVC
- ✅ **JWT Debugging**: Added comprehensive JWT troubleshooting tools and documentation  
- ✅ **Controller Standardization**: Implemented consistent ApiResponse format across all endpoints
- ✅ **Spring Security 6.x**: Updated to handle new authorization exception types
- ✅ **Documentation Reorganization**: Structured documentation into logical categories

### **Coming Soon**
- 🔜 **Performance Monitoring**: Documentation for application metrics and monitoring
- 🔜 **Deployment Guide**: Comprehensive production deployment documentation
- 🔜 **Testing Guide**: Unit testing and integration testing documentation

---

*This documentation is maintained by the BE-DMS development team. Last updated: August 26, 2025*
