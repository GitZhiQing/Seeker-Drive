import os
import shutil

from sqlalchemy.orm import Session

from app import settings
from app.database import SessionLocal


def db_init():
    if settings.ENV == "prod":
        db_init_prod()
    else:
        db_init_dev()


def db_init_dev():
    if os.path.exists("data.db"):
        os.remove("data.db")

    from app.database import engine, models

    models.Base.metadata.drop_all(bind=engine)
    models.Base.metadata.create_all(bind=engine)  # noqa

    from app.database import crud
    from app import schemas

    db: Session = SessionLocal()
    try:
        user_in = schemas.users.UserCreate(username="seeker", password="123456")
        existing_user = db.query(models.User).filter(models.User.username == user_in.username).first()  # type: ignore
        if not existing_user:
            user = crud.create_user(db, user=user_in)
            print(user)
        else:
            print(f"User with username '{user_in.username}' already exists.")
    finally:
        db.close()


def db_init_prod():
    # 判断是否存在数据库文件，不存在则创建
    if not os.path.exists("data.db"):
        from app.database import engine, models

        models.Base.metadata.create_all(bind=engine)  # noqa

        from app.database import crud
        from app import schemas

        db: Session = SessionLocal()
        try:
            user_in = schemas.users.UserCreate(username="seeker", password="123456")
            existing_user = (
                db.query(models.User)
                .filter(models.User.username == user_in.username)  # type: ignore
                .first()
            )  # type: ignore
            if not existing_user:
                user = crud.create_user(db, user=user_in)
                print(user)
            else:
                print(f"User with username '{user_in.username}' already exists.")
        finally:
            db.close()
    else:
        print("Database file 'data.db' already exists.")


def dir_init():
    if settings.ENV == "prod":
        dir_init_prod()
    else:
        dir_init_dev()


def dir_init_dev():
    # 判断是否存在 settings.STATIC_DIR & settings.DRIVE_DIR，不存在则创建，存在则删除并创建
    if os.path.exists(settings.STATIC_DIR):
        shutil.rmtree(settings.STATIC_DIR)
    if os.path.exists(settings.DRIVE_DIR):
        shutil.rmtree(settings.DRIVE_DIR)
    os.makedirs(settings.STATIC_DIR, exist_ok=True)
    os.makedirs(settings.DRIVE_DIR, exist_ok=True)


def dir_init_prod():
    # 判断是否存在 settings.STATIC_DIR & settings.DRIVE_DIR，不存在则创建
    if not os.path.exists(settings.STATIC_DIR):
        os.makedirs(settings.STATIC_DIR, exist_ok=True)
    if not os.path.exists(settings.DRIVE_DIR):
        os.makedirs(settings.DRIVE_DIR, exist_ok=True)
