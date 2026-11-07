provider "clevercloud" {}

# La autenticación se obtiene del entorno o de clever login.
# No incluir aquí credenciales de MySQL ni tokens del proveedor.
resource "clevercloud_mysql" "franquicia" {
  name   = var.database_service_name
  plan   = "dev"
  region = var.region

  lifecycle {
    prevent_destroy = true

    precondition {
      condition     = var.mode == "create" || var.existing_mysql_id != null
      error_message = "El modo adopt requiere existing_mysql_id. No se creará una base duplicada."
    }
  }
}
