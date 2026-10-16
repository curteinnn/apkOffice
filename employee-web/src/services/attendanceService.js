import { apiRequest } from "./api";

export function getMyAttendance() {
  return apiRequest("/api/attendance/my");
}

export function checkIn() {
  return apiRequest("/api/attendance/check-in", {
    method: "POST",
  });
}

export function checkOut() {
  return apiRequest("/api/attendance/check-out", {
    method: "POST",
  });
}
