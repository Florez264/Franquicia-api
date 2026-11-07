# Importar adopta la infraestructura que ya existe: no crea otra base.
import {
  for_each = var.mode == "adopt" && var.existing_mysql_id != null ? { current = var.existing_mysql_id } : {}
  to       = clevercloud_mysql.franquicia
  id       = each.value
}
