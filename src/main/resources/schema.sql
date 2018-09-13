CREATE TABLE IF NOT EXISTS tenants (
  id                         INTEGER     NOT NULL AUTO_INCREMENT,
  name                       VARCHAR(45) NOT NULL,
  api_key                    VARCHAR(50) NOT NULL,
  default_valid_time_minutes INTEGER     NOT NULL DEFAULT 10,
  key_pair_blob              MEDIUMBLOB  NOT NULL,
  cert_password              VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY (name),
  UNIQUE KEY (api_key)
);
CREATE TABLE IF NOT EXISTS users (
  id        INTEGER     NOT NULL AUTO_INCREMENT,
  name      VARCHAR(45) NOT NULL,
  password  VARCHAR(45) NOT NULL,
  tenant_id INTEGER     NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY user_name(name),
  UNIQUE KEY uni_user_tenant (id, tenant_id),
  KEY fk_tenant_id_idx (tenant_id),
  CONSTRAINT fk_tenant_id_idx FOREIGN KEY (tenant_id) REFERENCES tenants (id)
);
CREATE TABLE IF NOT EXISTS user_roles (
  id      INTEGER     NOT NULL AUTO_INCREMENT,
  user_id INTEGER     NOT NULL,
  role    VARCHAR(45) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uni_username_role (role, user_id),
  KEY fk_user_id_idx (user_id),
  CONSTRAINT fk_user_id_idx FOREIGN KEY (user_id) REFERENCES users (id)
);
