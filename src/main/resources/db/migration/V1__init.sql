CREATE TABLE PresentIdea
(
    id          UUID    NOT NULL,
    listId      UUID,
    name        VARCHAR(255),
    url         VARCHAR(255),
    description VARCHAR(255),
    importance  INTEGER NOT NULL,
    CONSTRAINT pk_presentidea PRIMARY KEY (id)
);

CREATE TABLE WishList
(
    id          UUID NOT NULL,
    name        VARCHAR(255),
    description VARCHAR(255),
    username    VARCHAR(255),
    displayName VARCHAR(255),
    active      BOOLEAN,
    expires     BIGINT,
    CONSTRAINT pk_wishlist PRIMARY KEY (id)
);