ALTER TABLE PresentIdea
    ADD claimed BOOLEAN;

ALTER TABLE PresentIdea
    ADD claimerName VARCHAR(255);

ALTER TABLE PresentIdea
    ALTER COLUMN claimed SET NOT NULL;