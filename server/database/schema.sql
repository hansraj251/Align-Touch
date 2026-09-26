CREATE TABLE IF NOT EXISTS auth_otps (
    id BIGSERIAL PRIMARY KEY,
    identifier VARCHAR(255) NOT NULL,
    identifier_type VARCHAR(20) NOT NULL,
    otp_hash TEXT NOT NULL,
    purpose VARCHAR(30) NOT NULL DEFAULT 'login',
    expires_at TIMESTAMPTZ NOT NULL,
    attempts INTEGER NOT NULL DEFAULT 0,
    used_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_auth_otp_identifier_type
        CHECK (
            identifier_type IN (
                'phone',
                'email'
            )
        ),

    CONSTRAINT chk_auth_otp_purpose
        CHECK (
            purpose IN (
                'login',
                'register'
            )
        )
);

CREATE INDEX IF NOT EXISTS idx_auth_otps_identifier
    ON auth_otps(identifier);

CREATE INDEX IF NOT EXISTS idx_auth_otps_expires_at
    ON auth_otps(expires_at);

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    phone VARCHAR(20) UNIQUE,
    email VARCHAR(255) UNIQUE,
    password_hash TEXT,
    display_name VARCHAR(100) NOT NULL,
    avatar_url TEXT,
    about TEXT,
    last_seen_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS user_devices (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    device_id VARCHAR(255) NOT NULL,
    device_name VARCHAR(255),
    platform VARCHAR(20) NOT NULL,
    push_token TEXT,
    last_active_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_user_devices_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_user_device
        UNIQUE (user_id, device_id)
);

CREATE TABLE IF NOT EXISTS conversations (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(20) NOT NULL DEFAULT 'direct',
    title VARCHAR(255),
    avatar_url TEXT,
    created_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_conversation_type
        CHECK (type IN ('direct', 'group'))
);

CREATE TABLE IF NOT EXISTS conversation_members (
    id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'member',
    joined_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_read_message_id BIGINT,
    cleared_at TIMESTAMPTZ,

    CONSTRAINT fk_members_conversation
        FOREIGN KEY (conversation_id)
        REFERENCES conversations(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_members_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_conversation_member
        UNIQUE (conversation_id, user_id)
);

CREATE TABLE IF NOT EXISTS messages (
    id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    message_type VARCHAR(20) NOT NULL DEFAULT 'text',
    content TEXT,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    reply_to_message_id BIGINT,
    forwarded_from_message_id BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    edited_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    expires_at TIMESTAMPTZ,

    CONSTRAINT fk_messages_conversation
        FOREIGN KEY (conversation_id)
        REFERENCES conversations(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_messages_sender
        FOREIGN KEY (sender_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_messages_reply
        FOREIGN KEY (reply_to_message_id)
        REFERENCES messages(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_message_type
        CHECK (
            message_type IN (
                'text',
                'image',
                'video',
                'audio',
                'document',
                'location',
                'contact'
            )
        )
);

CREATE INDEX IF NOT EXISTS idx_devices_user
    ON user_devices(user_id);

CREATE INDEX IF NOT EXISTS idx_members_conversation
    ON conversation_members(conversation_id);

CREATE INDEX IF NOT EXISTS idx_members_user
    ON conversation_members(user_id);

CREATE INDEX IF NOT EXISTS idx_messages_conversation
    ON messages(conversation_id, id);

CREATE INDEX IF NOT EXISTS idx_messages_sender
    ON messages(sender_id);

CREATE INDEX IF NOT EXISTS idx_messages_created
    ON messages(created_at);


CREATE TABLE IF NOT EXISTS message_reactions (
    id BIGSERIAL PRIMARY KEY,
    message_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    reaction VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_message_reactions_message
        FOREIGN KEY (message_id)
        REFERENCES messages(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_message_reactions_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_message_reaction_user
        UNIQUE (message_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_message_reactions_message
    ON message_reactions(message_id);

CREATE INDEX IF NOT EXISTS idx_message_reactions_user
    ON message_reactions(user_id);
