from pydantic import BaseModel
import time


class Token(BaseModel):
    access_token: str
    token_type: str


class TokenData(BaseModel):
    username: str
    uid: int
    exp: int = 7 * 24 * 60 * 60  # 过期时间
    iat: int = int(time.time())  # 签发时间
