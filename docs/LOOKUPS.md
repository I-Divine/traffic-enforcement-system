# Lookup Endpoints (Officers, Owners, Vehicles)

This document explains how officer, owner, and vehicle lookups work in this API.

**Base URL**
`http://localhost:8080/api/v1`

**Auth**
All endpoints below require a valid JWT in the `Authorization: Bearer <token>` header.

**Officers Lookup**
`GET /officers/badge/{badgeNumber}`
Roles allowed: `ADMIN`, `ROAD_OFFICERS`, `APPEAL_OFFICERS`, `REGISTRATION_OFFICERS`

Example:
```bash
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/api/v1/officers/badge/BN-12345
```

Response shape:
```json
{
  "userId": "uuid",
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane@example.com",
  "phoneNumber": "08000000000",
  "role": "ROAD_OFFICERS",
  "officerDetails": {
    "badgeNumber": "BN-12345",
    "department": "Traffic",
    "rank": "Inspector",
    "assignmentArea": "Ikeja"
  }
}
```

`GET /officers/search?department={department}`
Roles allowed: `ADMIN`, `ROAD_OFFICERS`, `APPEAL_OFFICERS`, `REGISTRATION_OFFICERS`

`GET /officers/search?assignmentArea={assignmentArea}`
Roles allowed: `ADMIN`, `ROAD_OFFICERS`, `APPEAL_OFFICERS`, `REGISTRATION_OFFICERS`

Notes:
`department` and `assignmentArea` are case-insensitive.
Only one of `department` or `assignmentArea` is allowed at a time.

**Owners Lookup**
`GET /owners/search?driversLicenseNumber={dlNumber}`
Roles allowed: `ADMIN`, `ROAD_OFFICERS`, `APPEAL_OFFICERS`, `REGISTRATION_OFFICERS`

`GET /owners/search?plateNumber={plateNumber}`
Roles allowed: `ADMIN`, `ROAD_OFFICERS`, `APPEAL_OFFICERS`, `REGISTRATION_OFFICERS`

Notes:
Provide exactly one query parameter, not both.
Looking up by `plateNumber` finds the vehicle first, then returns the owner profile.

Example:
```bash
curl -H "Authorization: Bearer <token>" \
  "http://localhost:8080/api/v1/owners/search?plateNumber=LAG-1234"
```

Response shape:
```json
{
  "userId": "uuid",
  "firstName": "Ayo",
  "lastName": "Balogun",
  "email": "ayo@example.com",
  "phoneNumber": "08000000000",
  "role": "OWNERS",
  "ownerDetails": {
    "address": "123 Example St",
    "city": "Lagos",
    "state": "Lagos",
    "driversLicenseNumber": "D12345678"
  },
  "vehicles": [
    {
      "vehicleId": "uuid",
      "plateNumber": "LAG-1234",
      "make": "Toyota",
      "model": "Corolla",
      "year": 2019,
      "color": "Blue",
      "registrationDate": "2025-01-01",
      "registrationExpiry": "2026-01-01"
    }
  ]
}
```

**Vehicles Lookup**
`GET /vehicles/me`
Roles allowed: `OWNERS`

This returns all vehicles owned by the authenticated user.

Example:
```bash
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/api/v1/vehicles/me
```

**Error Behavior**
`400 Bad Request` when required query params are missing or when multiple params are sent where only one is allowed.
`404 Not Found` when no officer/owner/vehicle matches the lookup.
