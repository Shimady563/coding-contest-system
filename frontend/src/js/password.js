export function validatePassword(password) {
  return {
    hasMinLength: password.length >= 8,
    hasUpperCase: /[A-Z]/.test(password),
    hasLowerCase: /[a-z]/.test(password),
    hasDigit: /\d/.test(password),
    hasSpecialChar: /[@#$%^&+=!?*]/.test(password),
    isValid:
      password.length >= 8 &&
      /[A-Z]/.test(password) &&
      /[a-z]/.test(password) &&
      /\d/.test(password) &&
      /[@#$%^&+=!?*]/.test(password),
  }
}
