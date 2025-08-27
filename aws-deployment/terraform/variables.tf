variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-west-2"
}

variable "db_password" {
  description = "RDS instance password"
  type        = string
  sensitive   = true
}