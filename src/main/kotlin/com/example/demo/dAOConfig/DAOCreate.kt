package com.example.demo.dAOConfig

import com.example.demo.model.Query

data class DAOCreate(
    private var path: String,
    private var pathChooseFile: String,
    private var fileName: String,
    private var fileChooseName: String,
    private var listQuery: List<Query>
) {
    fun getPath(): String = path
    fun setPath(newPath: String) {
        path = newPath
    }

    fun getPathChooseFile(): String = pathChooseFile
    fun setPathChooseFile(newPathChooseFile: String) {
        pathChooseFile = newPathChooseFile
    }

    fun getName(): String = fileName
    fun setName(newName: String) {
        fileName = newName
    }

    fun getFileChooseName(): String = fileChooseName
    fun setFileChooseName(newName: String) {
        fileName = newName
    }

    fun getListQuery(): List<Query> = listQuery
    fun setListQuery(newListQuery: List<Query>) {
        listQuery = newListQuery
    }
}
