variable "database_service_name" {
  description = "Nombre visible del servicio MySQL, no el nombre interno de la base."
  type        = string
  default     = "franquicia-db"
}

variable "region" {
  description = "Región real del servicio existente; mtl corresponde a Montreal."
  type        = string
  default     = "mtl"
}

variable "mode" {
  description = "adopt importa la base existente; create permite crear una base nueva de forma explícita."
  type        = string
  default     = "adopt"

  validation {
    condition     = contains(["adopt", "create"], var.mode)
    error_message = "mode debe ser adopt o create."
  }
}

variable "existing_mysql_id" {
  description = "ID mysql_... del servicio a importar; no el identificador addon_... ni el nombre de la base."
  type        = string
  default     = null

  validation {
    condition     = var.existing_mysql_id == null ? true : can(regex("^mysql_[0-9a-f-]+$", var.existing_mysql_id))
    error_message = "Usa el ID mysql_... mostrado en el dashboard de Clever Cloud."
  }
}
