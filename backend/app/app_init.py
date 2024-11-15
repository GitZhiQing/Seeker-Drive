import os

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
            existing_user = db.query(models.User).filter(
                models.User.username == user_in.username).first()  # type: ignore
            if not existing_user:
                user = crud.create_user(db, user=user_in)
                print(user)
            else:
                print(f"User with username '{user_in.username}' already exists.")
        finally:
            db.close()
    else:
        print("Database file 'data.db' already exists.")
