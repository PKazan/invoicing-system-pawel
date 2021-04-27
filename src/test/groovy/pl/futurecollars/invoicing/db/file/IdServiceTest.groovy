package pl.futurecollars.invoicing.db.file

import pl.futurecollars.invoicing.util.FilesService
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class IdServiceTest extends Specification {

    private Path nextIdPath = File.createTempFile('nextId', '.txt').toPath()

    def "when file is empty id is 1"() {
        given:
        IdService idService = new IdService(nextIdPath, new FilesService())

        expect:
        ["1"] == Files.readAllLines(nextIdPath)

        and:
        idService.getNextIdAndIncrement()
        ["2"] == Files.readAllLines(nextIdPath)

        and:
        idService.getNextIdAndIncrement()
        ["3"] == Files.readAllLines(nextIdPath)
    }

    def "when file is not empty the next id starts from last id"() {

        given:
        Files.writeString(nextIdPath, "10")

        FilesService filesService = new FilesService()
        IdService idService = new IdService(nextIdPath, filesService)

        expect:
        ["10"] == Files.readAllLines(nextIdPath)

        and:
        idService.getNextIdAndIncrement()
        ["11"] == Files.readAllLines(nextIdPath)

        and:
        idService.getNextIdAndIncrement()
        ["12"] == Files.readAllLines(nextIdPath)
    }
}
