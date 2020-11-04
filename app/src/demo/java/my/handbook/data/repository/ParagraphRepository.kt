package my.handbook.data.repository

import my.handbook.data.db.dao.ParagraphDao

class ParagraphRepository(paragraphDao: ParagraphDao) : BaseParagraphRepository(paragraphDao)
