# Traffic Enforcement System - Test Cases Documentation

## Table of Contents
1. [Test Case 1: Authentication & User Registration](#test-case-1-authentication--user-registration)
2. [Test Case 2: Violation Recording](#test-case-2-violation-recording)
3. [Test Case 3: Violation Search & Retrieval](#test-case-3-violation-search--retrieval)
4. [Test Case 4: Appeal Management](#test-case-4-appeal-management)
5. [Test Case 5: Payment Processing](#test-case-5-payment-processing)
6. [Test Case 6: Vehicle Registration & Management](#test-case-6-vehicle-registration--management)
7. [Test Case 7: Owner Lookup](#test-case-7-owner-lookup)
8. [Test Case 8: Officer Profile Lookup](#test-case-8-officer-profile-lookup)

---

## Test Case 1: Authentication & User Registration

### Test Case 1.1: User Registration

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | Registration Officer inputs user details (email, password, role) | System validates email format and password strength | ☐ |
| 2 | Click Register button with valid credentials | System creates new user account and returns authentication response | ☐ |
| 3 | Attempt registration with duplicate email | System rejects registration and displays error message | ☐ |
| 4 | Attempt registration with weak password | System validates password requirements and displays error | ☐ |
| 5 | Verify JWT token is generated in response | System returns valid JWT token for authenticated access | ☐ |

**Users Tested:** REGISTRATION_OFFICERS, ADMIN

---

### Test Case 1.2: User Login

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | User inputs email and correct password | System validates credentials against database | ☐ |
| 2 | Click Login button | System authenticates user and returns JWT token | ☐ |
| 3 | Login with correct email and wrong password | System denies access and displays error message | ☐ |
| 4 | Login with non-existent email | System returns authentication failure message | ☐ |
| 5 | Verify user role is included in authentication response | System returns user role with authentication token | ☐ |
| 6 | Verify JWT token can be used for subsequent API calls | System accepts valid token and grants access to protected endpoints | ☐ |

**Users Tested:** OWNERS, ROAD_OFFICERS, APPEAL_OFFICERS, REGISTRATION_OFFICERS, ADMIN

---

## Test Case 2: Violation Recording

### Test Case 2.1: Creating a New Violation

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | Road Officer logs in with valid credentials | System authenticates and grants access to violation recording | ☐ |
| 2 | Officer accesses violation creation form | System displays violation form with required fields (vehicle plate, violation type, location, fine amount) | ☐ |
| 3 | Officer enters all required violation details | System validates all input fields for completeness | ☐ |
| 4 | Officer selects violation type from dropdown | System loads corresponding fine amount for selected violation | ☐ |
| 5 | Click Submit button to record violation | System saves violation record and displays confirmation | ☐ |
| 6 | System verifies violation is associated with current officer | Violation record shows issuing officer's badge number and timestamp | ☐ |

**Users Tested:** ROAD_OFFICERS, APPEAL_OFFICERS

---

### Test Case 2.2: Violation Creation with Invalid Data

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | Officer submits violation form with missing required fields | System displays validation error for missing fields | ☐ |
| 2 | Officer enters invalid vehicle plate format | System rejects invalid plate number format | ☐ |
| 3 | Officer enters negative fine amount | System rejects invalid fine amount | ☐ |
| 4 | Officer submits form without selecting violation type | System prompts for violation type selection | ☐ |

**Users Tested:** ROAD_OFFICERS

---

## Test Case 3: Violation Search & Retrieval

### Test Case 3.1: Officer Viewing Issued Violations

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | Road Officer logs in successfully | System authenticates and grants access | ☐ |
| 2 | Officer navigates to "My Issued Violations" section | System displays list of violations issued by current officer | ☐ |
| 3 | Verify violation list shows violation details (plate, type, amount, date) | System displays complete violation information | ☐ |
| 4 | Officer reviews violation history | System shows all violations issued by officer in chronological order | ☐ |

**Users Tested:** ROAD_OFFICERS, APPEAL_OFFICERS

---

### Test Case 3.2: Owner Viewing Their Violations

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | Vehicle Owner logs in with valid credentials | System authenticates owner account | ☐ |
| 2 | Owner navigates to "My Violations" | System displays list of violations issued against owner's vehicles | ☐ |
| 3 | Verify all violations are linked to owner's registered vehicles | System shows only violations associated with owner's vehicles | ☐ |
| 4 | Owner can view violation details and fine amounts | System displays complete violation information | ☐ |

**Users Tested:** OWNERS

---

### Test Case 3.3: Searching Violations by Plate Number

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | Authorized officer enters valid plate number in search field | System displays search input with validation | ☐ |
| 2 | Click Search button | System searches database for violations matching plate | ☐ |
| 3 | System displays all violations for searched plate | Results show all violations with dates, types, and amounts | ☐ |
| 4 | Search with non-existent plate number | System returns empty results message | ☐ |
| 5 | Search with invalid plate format | System displays validation error | ☐ |

**Users Tested:** ADMIN, ROAD_OFFICERS, APPEAL_OFFICERS

---

## Test Case 4: Appeal Management

### Test Case 4.1: Creating an Appeal

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | Vehicle Owner logs in and navigates to violations | System displays owned violations with appeal option | ☐ |
| 2 | Owner clicks "Appeal" on a violation | System displays appeal form with fields for reason and supporting details | ☐ |
| 3 | Owner enters appeal reason and supporting details | System validates input and allows text entry | ☐ |
| 4 | Owner submits appeal | System creates appeal record and returns confirmation | ☐ |
| 5 | Verify appeal is linked to correct violation and owner | Appeal record shows owner, violation, and submission date | ☐ |
| 6 | Verify appeal status is set to PENDING | Appeal shows initial status of PENDING awaiting processing | ☐ |

**Users Tested:** OWNERS

---

### Test Case 4.2: Appeal Officer Viewing Appeals

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | Appeal Officer logs in with valid credentials | System authenticates officer account | ☐ |
| 2 | Officer navigates to "All Appeals" section | System displays list of all pending and processed appeals | ☐ |
| 3 | Officer reviews appeal details (owner, violation, reason) | System shows complete appeal information | ☐ |
| 4 | Verify appeals are sorted by submission date | Appeals displayed in chronological order by default | ☐ |

**Users Tested:** ADMIN, APPEAL_OFFICERS

---

### Test Case 4.3: Processing Appeals - Acceptance

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | Appeal Officer selects a pending appeal | System displays detailed appeal information | ☐ |
| 2 | Officer reviews appeal reason and evidence | System displays all submitted appeal details | ☐ |
| 3 | Officer clicks "Accept Appeal" button | System updates appeal status to ACCEPTED | ☐ |
| 4 | Verify violation is marked as resolved | Violation status changes accordingly after appeal acceptance | ☐ |
| 5 | Owner receives notification of appeal acceptance | System logs appeal acceptance event | ☐ |

**Users Tested:** ADMIN, APPEAL_OFFICERS

---

### Test Case 4.4: Processing Appeals - Rejection

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | Appeal Officer selects a pending appeal | System displays detailed appeal information | ☐ |
| 2 | Officer reviews evidence and determines appeal should be rejected | System ready for rejection action | ☐ |
| 3 | Officer clicks "Reject Appeal" button | System updates appeal status to REJECTED | ☐ |
| 4 | Verify violation remains active and requires payment | Fine amount remains applicable and in system | ☐ |
| 5 | Owner receives notification of appeal rejection | System logs appeal rejection event | ☐ |

**Users Tested:** ADMIN, APPEAL_OFFICERS

---

## Test Case 5: Payment Processing

### Test Case 5.1: Recording Payment by Owner

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | Vehicle Owner logs in and navigates to violations | System displays list of outstanding violations with fines | ☐ |
| 2 | Owner selects a violation with outstanding fine | System displays violation details and payment option | ☐ |
| 3 | Owner enters payment details (amount, payment method) | System validates payment information | ☐ |
| 4 | Owner submits payment | System processes payment and records transaction | ☐ |
| 5 | System verifies payment amount matches violation fine | Payment is validated to match the violation amount | ☐ |
| 6 | Verify payment receipt is generated | System returns payment confirmation with reference number | ☐ |
| 7 | Verify violation status changes to PAID | Violation record updated with payment status | ☐ |

**Users Tested:** OWNERS

---

### Test Case 5.2: Viewing All Payments

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | Authorized officer logs in | System authenticates access | ☐ |
| 2 | Officer navigates to Payments section | System displays list of all recorded payments | ☐ |
| 3 | System displays payment details (amount, date, owner, violation) | Complete payment information visible | ☐ |
| 4 | Verify payments are listed in chronological order | Payments sorted by date | ☐ |

**Users Tested:** ADMIN, ROAD_OFFICERS, APPEAL_OFFICERS

---

### Test Case 5.3: Payment Statistics by Period

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | Authorized officer selects payment period | System provides options (Daily, Weekly, Monthly, Yearly) | ☐ |
| 2 | Officer requests payment total for selected period | System calculates total payments for specified period | ☐ |
| 3 | System displays total revenue collected | Shows sum of all payments in selected period | ☐ |
| 4 | System displays number of payments processed | Shows payment count for the period | ☐ |
| 5 | Verify calculations are accurate | Math verified for sample data | ☐ |

**Users Tested:** ADMIN, ROAD_OFFICERS, APPEAL_OFFICERS

---

## Test Case 6: Vehicle Registration & Management

### Test Case 6.1: Owner Creating Vehicle Profile

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | Vehicle Owner logs in | System authenticates owner account | ☐ |
| 2 | Owner navigates to vehicle registration | System displays vehicle creation form | ☐ |
| 3 | Owner enters vehicle details (plate number, make, model, year, color) | System validates all input fields | ☐ |
| 4 | Owner submits vehicle information | System validates and accepts vehicle details | ☐ |
| 5 | System creates vehicle profile for owner | Vehicle record created and linked to owner | ☐ |
| 6 | Owner can view newly created vehicle in their profile | Vehicle appears in "My Vehicles" list | ☐ |

**Users Tested:** OWNERS

---

### Test Case 6.2: Registration Officer Registering Vehicle

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | Registration Officer logs in | System authenticates officer account | ☐ |
| 2 | Officer accesses vehicle registration section | System displays vehicle registration form | ☐ |
| 3 | Officer enters complete vehicle details | System validates engine number, chassis number, owner info | ☐ |
| 4 | Officer enters owner information (ID, name, contact) | System validates owner details | ☐ |
| 5 | Officer submits registration | System officially registers vehicle in system | ☐ |
| 6 | System generates registration confirmation | Registration record with unique identification created | ☐ |

**Users Tested:** ADMIN, REGISTRATION_OFFICERS

---

### Test Case 6.3: Updating Vehicle Information

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | Registration Officer selects registered vehicle | System displays current vehicle information | ☐ |
| 2 | Officer modifies vehicle details (color, owner, etc.) | System allows editing of vehicle information | ☐ |
| 3 | Officer submits updated information | System validates and saves changes | ☐ |
| 4 | Verify updated information is reflected in system | Changes persist and appear in vehicle profile | ☐ |
| 5 | System logs update with timestamp and officer ID | Update history recorded in audit trail | ☐ |

**Users Tested:** ADMIN, REGISTRATION_OFFICERS

---

### Test Case 6.4: Owner Viewing Their Vehicles

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | Vehicle Owner logs in | System authenticates owner | ☐ |
| 2 | Owner navigates to "My Vehicles" | System displays list of owner's vehicles | ☐ |
| 3 | System displays each vehicle with complete details | Vehicle information shown (plate, make, model, status) | ☐ |
| 4 | Owner can access violations for each vehicle | Each vehicle shows associated violations count | ☐ |

**Users Tested:** OWNERS

---

## Test Case 7: Owner Lookup

### Test Case 7.1: Search Owner by Driver's License Number

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | Authorized officer enters driver's license number | System accepts input and validates format | ☐ |
| 2 | Officer clicks Search button | System searches database for matching owner record | ☐ |
| 3 | System displays owner details (name, contact, vehicles, violations) | Owner information returned with associated data | ☐ |
| 4 | Search with invalid license number | System returns "not found" message | ☐ |
| 5 | Search results include all owner's registered vehicles | Account shows vehicle list | ☐ |

**Users Tested:** ADMIN, ROAD_OFFICERS, APPEAL_OFFICERS, REGISTRATION_OFFICERS

---

### Test Case 7.2: Search Owner by Vehicle Plate Number

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | Authorized officer enters vehicle plate number | System accepts and validates plate format | ☐ |
| 2 | Officer clicks Search button | System locates vehicle and returns owner information | ☐ |
| 3 | System displays owner details and vehicle information | Complete owner and vehicle details returned | ☐ |
| 4 | Search with non-existent plate | System returns "vehicle not found" message | ☐ |
| 5 | Verify all violations for owner are accessible | Violation history linked to owner shown | ☐ |

**Users Tested:** ADMIN, ROAD_OFFICERS, APPEAL_OFFICERS, REGISTRATION_OFFICERS

---

### Test Case 7.3: Search Validation

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | Officer submits search with both parameters | System rejects and displays error "Provide only one search parameter" | ☐ |
| 2 | Officer submits search with neither parameter | System rejects and displays error "Provide driversLicenseNumber or plateNumber" | ☐ |
| 3 | Officer submits valid single parameter | System processes search successfully | ☐ |

**Users Tested:** ADMIN, ROAD_OFFICERS, APPEAL_OFFICERS, REGISTRATION_OFFICERS

---

## Test Case 8: Officer Profile Lookup

### Test Case 8.1: Get Officer by Badge Number

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | Authorized user enters officer badge number | System accepts badge number input | ☐ |
| 2 | System searches for officer with matching badge | Database queried for officer record | ☐ |
| 3 | System displays officer profile (name, badge, department, assignment area) | Complete officer information returned | ☐ |
| 4 | Verify officer's assignment area is shown | Assignment information displayed in profile | ☐ |
| 5 | Search with invalid badge number | System returns "officer not found" message | ☐ |

**Users Tested:** ADMIN, ROAD_OFFICERS, APPEAL_OFFICERS, REGISTRATION_OFFICERS

---

### Test Case 8.2: Search Officers by Department

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | Authorized user selects search by department | System displays department selection interface | ☐ |
| 2 | User selects or enters department name | System validates department input | ☐ |
| 3 | Click Search button | System returns all officers in selected department | ☐ |
| 4 | System displays list of all matching officers | Officer list with names, badges, and assignments shown | ☐ |
| 5 | Search with non-existent department | System returns "no officers found" message | ☐ |

**Users Tested:** ADMIN, ROAD_OFFICERS, APPEAL_OFFICERS, REGISTRATION_OFFICERS

---

### Test Case 8.3: Search Officers by Assignment Area

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | Authorized user selects search by assignment area | System displays assignment area selection interface | ☐ |
| 2 | User enters assignment area | System validates input | ☐ |
| 3 | Click Search button | System returns all officers assigned to area | ☐ |
| 4 | System displays list of matching officers | Officer list with details displayed | ☐ |
| 5 | Verify officer contact information is accessible | Officer details include contact information | ☐ |

**Users Tested:** ADMIN, ROAD_OFFICERS, APPEAL_OFFICERS, REGISTRATION_OFFICERS

---

### Test Case 8.4: Officer Search Validation

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | User submits search with both department and assignment area | System rejects and displays error "Provide only one search parameter" | ☐ |
| 2 | User submits search with neither parameter | System rejects and displays error "Provide department or assignmentArea" | ☐ |
| 3 | User submits valid single parameter | System processes search successfully | ☐ |

**Users Tested:** ADMIN, ROAD_OFFICERS, APPEAL_OFFICERS, REGISTRATION_OFFICERS

---

## Authorization & Access Control Tests

### Test Case 9: Role-Based Access Control

| Step | Test Description | Expected Result | Result |
|------|------------------|-----------------|--------|
| 1 | OWNER user attempts to create a violation | System denies access and returns 403 Forbidden | ☐ |
| 2 | OWNER user attempts to register a vehicle as officer | System denies access and returns 403 Forbidden | ☐ |
| 3 | ROAD_OFFICER attempts to accept an appeal | System denies access and returns 403 Forbidden | ☐ |
| 4 | APPEAL_OFFICER attempts to issue a violation | System allows if user has ROAD_OFFICERS role included | ☐ |
| 5 | Non-authenticated user attempts API call | System returns 401 Unauthorized | ☐ |
| 6 | User with expired JWT token attempts access | System rejects token and returns 401 Unauthorized | ☐ |

**Users Tested:** All roles

---

## Notes & Observations

- All tests should verify appropriate error messages are displayed
- Timestamp accuracy should be verified for all operations
- Database integrity should be verified after each transaction
- Ensure JWT tokens include necessary claims for role verification
- Audit logs should record all state-changing operations
- Test both success and failure paths for critical features

---

**Document Version:** 1.0  
**Last Updated:** March 23, 2026  
**Test Coverage:** Core Features - Authentication, Violations, Appeals, Payments, Vehicles, Lookups
