package com.portkiller.util;

import com.portkiller.constants.AppConstants;

public class ValidationUtil {

    public static ValidationResult validatePort(String portStr) {
        if (portStr == null || portStr.trim().isEmpty()) {
            return new ValidationResult(false, AppConstants.MSG_EMPTY_PORT);
        }

        if (!portStr.matches("\\d+")) {
            return new ValidationResult(false, AppConstants.MSG_INVALID_INPUT);
        }

        try {
            int port = Integer.parseInt(portStr);
            if (port < AppConstants.MIN_PORT || port > AppConstants.MAX_PORT) {
                return new ValidationResult(false, AppConstants.MSG_INVALID_PORT);
            }
            return new ValidationResult(true, "Valid");
        } catch (NumberFormatException e) {
            return new ValidationResult(false, AppConstants.MSG_INVALID_INPUT);
        }
    }

    public static class ValidationResult {
        private boolean valid;
        private String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }
}