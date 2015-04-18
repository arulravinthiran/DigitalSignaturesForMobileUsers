SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema digsigmobile
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `digsigmobile` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `digsigmobile` ;

-- -----------------------------------------------------
-- Table `digsigmobile`.`tbl_UserDetails`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `digsigmobile`.`tbl_UserDetails` (
  `UserID` INT(11) NOT NULL,
  `Name` VARCHAR(45) NOT NULL,
  `FamilyName` VARCHAR(45) NULL,
  `PrimaryMobileNumber` VARCHAR(20) NOT NULL,
  `SecondaryMobileNumber` VARCHAR(20) NULL,
  `PrimaryEmailID` VARCHAR(60) NOT NULL,
  `SecondaryEmailID` VARCHAR(60) NULL,
  `HasCertificate` TINYINT(1) NOT NULL DEFAULT 0,
  `Country` VARCHAR(45) NOT NULL,
  `State` VARCHAR(45) NOT NULL,
  `City` VARCHAR(45) NOT NULL,
  `ZipCode` VARCHAR(10) NOT NULL,
  `Street` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`UserID`),
  UNIQUE INDEX `PrimaryMobileNumber_UNIQUE` (`PrimaryMobileNumber` ASC),
  UNIQUE INDEX `PrimaryEmailID_UNIQUE` (`PrimaryEmailID` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `digsigmobile`.`tbl_DocumentDetails`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `digsigmobile`.`tbl_DocumentDetails` (
  `TrustCode` INT(11) NOT NULL,
  `OrigFile` MEDIUMBLOB NOT NULL,
  `UploadedTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `MIMEType` VARCHAR(25) NOT NULL,
  `InitiatorUserID` INT(11) NOT NULL,
  PRIMARY KEY (`TrustCode`),
  INDEX `fk_tbl_DocumentDetails_tbl_UserDetails_idx` (`InitiatorUserID` ASC),
  CONSTRAINT `fk_tbl_DocumentDetails_tbl_UserDetails`
    FOREIGN KEY (`InitiatorUserID`)
    REFERENCES `digsigmobile`.`tbl_UserDetails` (`UserID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `digsigmobile`.`tbl_DigitalSignatureDetails`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `digsigmobile`.`tbl_DigitalSignatureDetails` (
  `UserID` INT(11) NOT NULL,
  `TrustCode` INT(11) NOT NULL,
  `SignedFile` VARCHAR(255) NULL,
  `CreationTime` TIMESTAMP  NULL,
  `ExpiryTime` TIMESTAMP  NULL,
  `HasSigned` TINYINT(1) NOT NULL DEFAULT 0,
  `IsValid` TINYINT(1)  NULL, 
  `ReasonForSigning` VARCHAR(45) NOT NULL DEFAULT 'Not Applicable',
  PRIMARY KEY (`UserID`, `TrustCode`),
  INDEX `fk_tbl_DigitalSignatureDetails_tbl_UserDetails1_idx` (`UserID` ASC),
  INDEX `fk_tbl_DigitalSignatureDetails_tbl_DocumentDetails1_idx` (`TrustCode` ASC),
  CONSTRAINT `fk_tbl_DigitalSignatureDetails_tbl_UserDetails1`
    FOREIGN KEY (`UserID`)
    REFERENCES `digsigmobile`.`tbl_UserDetails` (`UserID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_tbl_DigitalSignatureDetails_tbl_DocumentDetails1`
    FOREIGN KEY (`TrustCode`)
    REFERENCES `digsigmobile`.`tbl_DocumentDetails` (`TrustCode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `digsigmobile`.`tbl_CertificateDetails`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `digsigmobile`.`tbl_CertificateDetails` (
  `CertificateID` INT(11) NOT NULL,
  `PublicKeyN` VARCHAR(45) NOT NULL,
  `PublicKeyE` VARCHAR(255) NOT NULL,
  `CreationTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ExpiryTime` TIMESTAMP NOT NULL,
  `UserID` INT(11) NOT NULL,
  `CertificateFile` MEDIUMBLOB NOT NULL,
  PRIMARY KEY (`CertificateID`),
  INDEX `fk_tbl_CertificateDetails_tbl_UserDetails1_idx` (`UserID` ASC),
  CONSTRAINT `fk_tbl_CertificateDetails_tbl_UserDetails1`
    FOREIGN KEY (`UserID`)
    REFERENCES `digsigmobile`.`tbl_UserDetails` (`UserID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

ALTER TABLE tbl_CertificateDetails MODIFY  ExpiryTime TIMESTAMP NOT NULL DEFAULT DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL 1 YEAR)  
-- -----------------------------------------------------
-- Table `digsigmobile`.`tbl_RevokedCertificateDetails`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `digsigmobile`.`tbl_RevokedCertificateDetails` (
  `RevocationID` INT(11) NOT NULL,
  `ReasonForRevocation` VARCHAR(155) NOT NULL DEFAULT 'Certificate Expired',
  `CertificateID` INT(11) NOT NULL,
  `RevokedTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (`RevocationID`),
  INDEX `fk_tbl_RevokedCertificateDetails_tbl_CertificateDetails1_idx` (`CertificateID` ASC),
  CONSTRAINT `fk_tbl_RevokedCertificateDetails_tbl_CertificateDetails1`
    FOREIGN KEY (`CertificateID`)
    REFERENCES `digsigmobile`.`tbl_CertificateDetails` (`CertificateID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

