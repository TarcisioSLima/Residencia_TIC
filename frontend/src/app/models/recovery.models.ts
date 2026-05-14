export interface RecoveryRequestDto {
  email: string;
}

export interface VerifyCodeRequestDto {
  email: string;
  code: string;
}

export interface VerifyCodeResponseDto {
  resetToken: string;
}

export interface ResetPasswordRequestDto {
  resetToken: string;
  newPassword: string;
}
