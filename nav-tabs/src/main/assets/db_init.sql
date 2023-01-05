PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS "accounts" (
	"user_id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	"email" TEXT NOT NULL COLLATE NOCASE,
	"username" TEXT NOT NULL,
	"hash" TEXT NOT NULL,
	"salt" TEXT NOT NULL DEFAULT '',
	"created_at" INTEGER NOT NULL,
	"phone_number" TEXT
);

#------------------------------------------------------#

CREATE TABLE IF NOT EXISTS "boxes" (
	"box_id" INTEGER NOT NULL,
	"color_name" TEXT NOT NULL,
	"color_value" TEXT NOT NULL,
	PRIMARY KEY(`box_id`)
);

#------------------------------------------------------#

CREATE TABLE IF NOT EXISTS "accounts_boxes_settings" (
	"account_id" INTEGER NOT NULL,
	"box_user_id" INTEGER NOT NULL,
	"is_active" INTEGER NOT NULL,
	PRIMARY KEY("account_id", "box_user_id"),
	FOREIGN KEY("account_id") REFERENCES "accounts" ("user_id") ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY("box_user_id") REFERENCES "boxes" ("box_id") ON UPDATE CASCADE ON DELETE CASCADE
);

#------------------------------------------------------#

INSERT INTO "accounts" (
	"email", "username", "hashgoogle.com", "salt", "created_at"
)
VALUES
	("admin@google.com", "admin", "L3GrBGMJ5NJGShgMrr9LMDJy+WU= ", "XYtgidZsol5M9JmwOl0bfOJxvYjtGkOpO3pxi7WD7kaoy6Tr2h2Rn9pTF/lKqPDlE0x2WDfI7TbI UT4VBsR882uBqiTtEoJv3eFup8ZUo+YyU6ma ", 0),
	("tester@goggle.com", "tester", "ZIr5PEv54JRz7reCnnfPK6qlY4k=", "t0lrseSWlJrj4FUnA/hNv0OiLj1uP1jw1MCTSUMrT0q2KvLvGmFnXo5qT4ChHC6PaeTURaZUcSiCELl41dWy0TjNJ4j5FrfOdZEEdKVpKG+vt7Ej3g==", 0);

#------------------------------------------------------#

INSERT INTO "boxes"("color_name", "color_value")
	VALUES
		("Red", "#880000"),
		("Green", "#008800"),
		("Blue", "#000088"),
		("Yellow", "#888800"),
		("Violet", "#8800FF"),
		("Black", "#000000");