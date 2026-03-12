
ALTER TABLE tb_cliente ADD CONSTRAINT uk_cliente_telefone UNIQUE (telefone);

ALTER TABLE tb_cliente ADD CONSTRAINT uk_cliente_email UNIQUE (email);