CREATE TABLE IF NOT EXISTS "drzava" (
	"id"	INTEGER,
	"naziv"	TEXT,
	"glavni_grad"	INTEGER,
	PRIMARY KEY("id"),
	FOREIGN KEY("glavni_grad") REFERENCES "grad"("id")
);
CREATE TABLE IF NOT EXISTS "grad" (
	"id"	INTEGER,
	"naziv"	TEXT,
	"broj_stanovnika"	INTEGER,
	"drzava"	INTEGER,
	PRIMARY KEY("id"),
	FOREIGN KEY("drzava") REFERENCES "drzava"("id")
);
INSERT INTO "drzava" VALUES (1,'Bosna i Hercegovina',1);
INSERT INTO "drzava" VALUES (2,'Hrvatska',2);
INSERT INTO "drzava" VALUES (3,'Srbija',3);
INSERT INTO "grad" VALUES (1,'Sarajevo',500000,1);
INSERT INTO "grad" VALUES (2,'Zagreb',800000,2);
INSERT INTO "grad" VALUES (3,'Beograd',1500000,3);
