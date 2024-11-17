import time

from sqlalchemy import Column, Integer, String, ForeignKey
from sqlalchemy.orm import relationship

from app.database import Base


class User(Base):
    __tablename__ = "user"

    uid = Column(Integer, primary_key=True, autoincrement=True)
    avatar = Column(String, nullable=True)
    username = Column(String, unique=True, nullable=False)
    hashed_password = Column(String, nullable=False)
    register_time = Column(String, default=str(int(time.time())))

    files = relationship("File", back_populates="user")


class File(Base):
    __tablename__ = "file"

    fid = Column(Integer, primary_key=True, autoincrement=True)
    name = Column(String, nullable=False)
    size = Column(Integer, nullable=False)
    status = Column(Integer, nullable=False, default=0)
    hash = Column(String, nullable=False)
    upload_time = Column(String, default=str(int(time.time())))
    user_id = Column(
        Integer, ForeignKey("user.uid", ondelete="CASCADE"), nullable=False
    )

    user = relationship("User", back_populates="files")
