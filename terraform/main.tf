provider "aws" {
  region = "us-east-1"
}

resource "aws_db_instance" "franquicia_db" {
  allocated_storage    = 20
  storage_type         = "gp2"
  engine               = "mysql"
  engine_version       = "8.0"
  instance_class       = "db.t2.micro"
  name                 = "franquiciasdb"
  username             = "root"
  password             = "123"
  publicly_accessible  = true
  skip_final_snapshot  = true
}