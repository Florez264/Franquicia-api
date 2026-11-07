output "database_host" {
  description = "Host para conectar la API y DBeaver."
  value       = clevercloud_mysql.franquicia.host
}

output "database_name" {
  value = clevercloud_mysql.franquicia.database
}

output "database_user" {
  value = clevercloud_mysql.franquicia.user
}

output "database_port" {
  value = clevercloud_mysql.franquicia.port
}

output "r2dbc_url" {
  description = "DB_URL para el perfil cloud; TLS lo exige application-cloud.properties."
  value       = "r2dbc:mysql://${clevercloud_mysql.franquicia.host}:${clevercloud_mysql.franquicia.port}/${clevercloud_mysql.franquicia.database}"
}
