rootProject.name = "CytoidInfo"
include("src:test")
findProject(":src:test")?.name = "test"
