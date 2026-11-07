terraform {
  required_version = ">= 1.7.0, < 2.0.0"

  required_providers {
    clevercloud = {
      source  = "CleverCloud/clevercloud"
      version = "3.0.0"
    }
  }
}
