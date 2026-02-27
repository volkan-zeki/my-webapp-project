<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>TODO List</title>
<style>
  body { font-family: Arial, sans-serif; margin: 2em; }
  ul { list-style: none; padding: 0; }
  li { margin: 0.5em 0; }
  button.delete { margin-left: 1em; }
</style>
</head>
<body>
<h1>TODO List</h1>
<div>
  <input id="newTask" type="text" placeholder="Enter new task" />
  <button id="addBtn">Add</button>
</div>
<ul id="tasks"></ul>

<script>
  // use absolute path so requests hit the servlet mapping regardless of context
  const api = '/api/tasks';

  async function loadTasks() {
    const res = await fetch(api);
    const tasks = await res.json();
    const ul = document.getElementById('tasks');
    ul.innerHTML = '';
    tasks.forEach(t => {
      const li = document.createElement('li');
      li.textContent = t.description;
      const btn = document.createElement('button');
      btn.textContent = 'Delete';
      btn.className = 'delete';
      btn.onclick = () => deleteTask(t.id);
      li.appendChild(btn);
      ul.appendChild(li);
    });
  }

  async function addTask() {
    const input = document.getElementById('newTask');
    const desc = input.value.trim();
    if (!desc) return;
    await fetch(api, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ description: desc })
    });
    input.value = '';
    loadTasks();
  }

  async function deleteTask(id) {
    await fetch(api + '?id=' + id, { method: 'DELETE' });
    loadTasks();
  }

  document.getElementById('addBtn').onclick = addTask;
  document.getElementById('newTask').addEventListener('keypress', e => {
    if (e.key === 'Enter') addTask();
  });

  loadTasks();
</script>
</body>
</html>