from sqlalchemy.orm import Session

from app import schemas, security
from app.database import models
from app.schemas.users import User
from app.security import get_password_hash


def get_user_by_uid(db: Session, uid: int):
    return db.query(models.User).filter(models.User.uid == uid).first()  # type: ignore


def get_user_by_username(db: Session, username: str):
    return db.query(models.User).filter(models.User.username == username).first()  # type: ignore


def create_user(db: Session, user: schemas.users.UserCreate):
    hashed_password = security.get_password_hash(user.password)
    db_user = models.User(username=user.username, hashed_password=hashed_password)
    db.add(db_user)
    db.commit()
    db.refresh(db_user)
    return db_user


def update_user_password(db: Session, user: User, new_password: str) -> User:
    user.hashed_password = get_password_hash(new_password)
    db.add(user)
    db.commit()
    db.refresh(user)
    return user


def update_user_username(db: Session, user: User, new_username: str) -> User:
    user.username = new_username
    db.add(user)
    db.commit()
    db.refresh(user)
    return user


def update_user_avatar(db: Session, user: User, file_path: str):
    user.avatar = file_path
    db.commit()
    db.refresh(user)
    return user


def get_files_by_uid(db: Session, skip: int = 0, limit: int = 100, user_id: int = None):
    return db.query(models.File).filter(models.File.user_id == user_id).offset(skip).limit(limit).all()  # type: ignore


def post_file_by_uid(db: Session, file: schemas.files.FileCreate):
    db_file = models.File(**file.dict())
    db.add(db_file)
    db.commit()
    db.refresh(db_file)
    return db_file


def get_file_by_fid(db: Session, fid: int):
    return db.query(models.File).filter(models.File.fid == fid).first()  # type: ignore


def delete_file_by_fid(db: Session, fid: int):
    db_file = db.query(models.File).filter(models.File.fid == fid).first()  # type: ignore
    if db_file:
        db.delete(db_file)
        db.commit()
