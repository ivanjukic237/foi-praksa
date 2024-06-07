CREATE TABLE IF NOT EXISTS KORISNICI (
                                         ID INT AUTO_INCREMENT PRIMARY KEY,
                                         NAME VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS PRODUCTS_METADATA (
                                               ID INT AUTO_INCREMENT PRIMARY KEY,
                                               TITLE VARCHAR(255),
                                               CREATED_TIME DATE
);

CREATE TABLE IF NOT EXISTS PRODUCTS (
                                        ID INT AUTO_INCREMENT PRIMARY KEY,
                                        NAME VARCHAR(255),
                                        PRICE DECIMAL(10, 2),
                                        PRODUCTS_METADATA_ID INT,
                                        FOREIGN KEY (PRODUCTS_METADATA_ID) REFERENCES PRODUCTS_METADATA(ID)
);