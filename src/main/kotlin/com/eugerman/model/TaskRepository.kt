package com.eugerman.model

object TaskRepository {

    private val tasks = mutableListOf(
        Task("cleaning", "Clean the house", Priority.Low),
        Task("gardening", "Mow the lawn", Priority.Medium),
        Task("shopping", "Buy the groceries", Priority.High),
        Task("painting", "Paint the fence", Priority.Medium)
    )

    fun allTasks(): List<Task> = tasks

    fun withPriority(priority: Priority): List<Task> =
        tasks.filter { it.priority == priority }

    fun withName(name: String): Task? =
        tasks.find { it.name.equals(name, ignoreCase = true) }

    fun add(task: Task): Unit {
        require(withName(task.name) == null ) {
            "Cannot duplicate task names!"
        }
        tasks.add(task)
    }

    fun remove(name: String) = tasks.removeIf { it.name == name }
}
