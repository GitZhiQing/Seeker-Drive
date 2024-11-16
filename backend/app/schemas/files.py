from pydantic import BaseModel


class FileBase(BaseModel):
    name: str
    size: int
    status: int
    hash: str
    user_id: int


class FileCreate(FileBase):
    pass


class File(FileBase):
    fid: int
    upload_time: str

    class Config:
        from_attributes = True
