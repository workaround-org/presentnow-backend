CREATE TABLE MagicLinkToken
(
    id        UUID         NOT NULL,
    email     VARCHAR(255) NOT NULL,
    tokenHash VARCHAR(64)  NOT NULL,
    createdAt BIGINT       NOT NULL,
    expiresAt BIGINT       NOT NULL,
    used      BOOLEAN      NOT NULL,
    CONSTRAINT pk_magiclinktoken PRIMARY KEY (id)
);

CREATE UNIQUE INDEX idx_magiclinktoken_hash ON MagicLinkToken (tokenHash);
CREATE INDEX idx_magiclinktoken_email ON MagicLinkToken (email);
