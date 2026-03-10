CREATE TABLE tb_empresa (
    id UUID PRIMARY KEY NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP,
    criado_por VARCHAR(255),
    atualizado_por VARCHAR(255),
    nome VARCHAR(150) NOT NULL,
    url_logo VARCHAR(255)
);


CREATE TABLE tb_usuario (
    id UUID PRIMARY KEY NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP,
    criado_por VARCHAR(255),
    atualizado_por VARCHAR(255),
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil VARCHAR(20) NOT NULL,
    empresa_id UUID NOT NULL,
    CONSTRAINT fk_usuario_empresa FOREIGN KEY (empresa_id) REFERENCES tb_empresa(id)
);

CREATE TABLE tb_vendedor (
    id UUID PRIMARY KEY NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP,
    criado_por VARCHAR(255),
    atualizado_por VARCHAR(255),
    nome VARCHAR(100) NOT NULL,
    cargo VARCHAR(50),
    data_nascimento DATE,
    empresa_id UUID NOT NULL,
    CONSTRAINT fk_vendedor_empresa FOREIGN KEY (empresa_id) REFERENCES tb_empresa(id)
);

CREATE TABLE tb_cliente (
    id UUID PRIMARY KEY NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP,
    criado_por VARCHAR(255),
    atualizado_por VARCHAR(255),
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(255),
    telefone VARCHAR(255),
    data_nascimento DATE,
    empresa_id UUID NOT NULL,
    CONSTRAINT fk_cliente_empresa FOREIGN KEY (empresa_id) REFERENCES tb_empresa(id)
);

CREATE TABLE tb_produto (
    id UUID PRIMARY KEY NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP,
    criado_por VARCHAR(255),
    atualizado_por VARCHAR(255),
    nome VARCHAR(150) NOT NULL,
    preco NUMERIC(10,2) NOT NULL,
    segmento VARCHAR(100),
    url_foto VARCHAR(255),
    codigo_barras VARCHAR(100),
    empresa_id UUID NOT NULL,
    CONSTRAINT fk_produto_empresa FOREIGN KEY (empresa_id) REFERENCES tb_empresa(id)
);

CREATE TABLE tb_conta (
    id UUID PRIMARY KEY NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP,
    criado_por VARCHAR(255),
    atualizado_por VARCHAR(255),
    empresa_id UUID NOT NULL UNIQUE,
    saldo_atual NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_conta_empresa FOREIGN KEY (empresa_id) REFERENCES tb_empresa(id)
);

CREATE TABLE tb_forma_pagamento (
    id UUID PRIMARY KEY NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP,
    criado_por VARCHAR(255),
    atualizado_por VARCHAR(255),
    nome VARCHAR(100) NOT NULL,
    tipo_base VARCHAR(20) NOT NULL,
    empresa_id UUID NOT NULL,
    CONSTRAINT fk_forma_pagamento_empresa FOREIGN KEY (empresa_id) REFERENCES tb_empresa(id)
);

CREATE TABLE tb_venda (
    id UUID PRIMARY KEY NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP,
    criado_por VARCHAR(255),
    atualizado_por VARCHAR(255),
    valor_total NUMERIC(15,2) NOT NULL,
    cliente_id UUID,
    vendedor_id UUID,
    empresa_id UUID NOT NULL,
    forma_pagamento_id UUID NOT NULL,
    CONSTRAINT fk_venda_cliente FOREIGN KEY (cliente_id) REFERENCES tb_cliente(id),
    CONSTRAINT fk_venda_vendedor FOREIGN KEY (vendedor_id) REFERENCES tb_vendedor(id),
    CONSTRAINT fk_venda_empresa FOREIGN KEY (empresa_id) REFERENCES tb_empresa(id),
    CONSTRAINT fk_venda_forma_pagamento FOREIGN KEY (forma_pagamento_id) REFERENCES tb_forma_pagamento(id)
);

CREATE TABLE tb_item_venda (
    id UUID PRIMARY KEY NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP,
    criado_por VARCHAR(255),
    atualizado_por VARCHAR(255),
    quantidade INTEGER NOT NULL,
    preco_unitario NUMERIC(10,2) NOT NULL,
    venda_id UUID NOT NULL,
    produto_id UUID NOT NULL,
    CONSTRAINT fk_item_venda_venda FOREIGN KEY (venda_id) REFERENCES tb_venda(id),
    CONSTRAINT fk_item_venda_produto FOREIGN KEY (produto_id) REFERENCES tb_produto(id)
);

CREATE TABLE tb_lancamento_financeiro (
    id UUID PRIMARY KEY NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP,
    criado_por VARCHAR(255),
    atualizado_por VARCHAR(255),
    tipo_operacao VARCHAR(20) NOT NULL,
    valor NUMERIC(15,2) NOT NULL,
    descricao VARCHAR(200) NOT NULL,
    conta_id UUID NOT NULL,
    venda_id UUID,
    CONSTRAINT fk_lancamento_conta FOREIGN KEY (conta_id) REFERENCES tb_conta(id),
    CONSTRAINT fk_lancamento_venda FOREIGN KEY (venda_id) REFERENCES tb_venda(id)
);