from typing import Union

from pydantic import BaseModel


class UserBase(BaseModel):
    username: str


class UserCreate(UserBase):
    password: str


class User(UserBase):
    uid: int
    avatar: Union[str, None]
    register_time: str

    class Config:
        from_attributes = True



class UserInDB(User):
    hashed_password: str


class UserPasswordUpdate(BaseModel):
    old_password: str
    new_password: str


class UserUsernameUpdate(BaseModel):
    username: str
