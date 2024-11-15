import time

from sqlalchemy import Column, Integer, String, ForeignKey, DateTime
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
    folders = relationship("Folder", back_populates="user")


class File(Base):
    __tablename__ = "file"

    fid = Column(Integer, primary_key=True, autoincrement=True)
    name = Column(String, nullable=False)
    size = Column(Integer, nullable=False)
    type = Column(String, nullable=False)
    status = Column(Integer, nullable=False)
    hash = Column(String, nullable=False)
    upload_time = Column(String, default=str(int(time.time())))
    folder_id = Column(
        Integer, ForeignKey("folder.foid", ondelete="CASCADE"), nullable=False
    )
    user_id = Column(
        Integer, ForeignKey("user.uid", ondelete="CASCADE"), nullable=False
    )

    user = relationship("User", back_populates="files")
    folder = relationship("Folder", back_populates="files")


class Folder(Base):
    __tablename__ = "folder"

    foid = Column(Integer, primary_key=True, autoincrement=True)
    name = Column(String, nullable=False)
    parent_id = Column(
        Integer, ForeignKey("folder.foid", ondelete="CASCADE"), nullable=True
    )
    user_id = Column(
        Integer, ForeignKey("user.uid", ondelete="CASCADE"), nullable=False
    )

    user = relationship("User", back_populates="folders")
    files = relationship("File", back_populates="folder")
    subfolders = relationship("Folder", back_populates="parent", remote_side=[foid])  # type: ignore
    parent = relationship("Folder", back_populates="subfolders", remote_side=[parent_id])  # type: ignore
