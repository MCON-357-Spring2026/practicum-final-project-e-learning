<template>
  <div class="unauthorized">
    <h1>403</h1>
    <p>You don't have permission to access this page.</p>
    <router-link to="/login" class="btn">Login</router-link>
  </div>
</template>

<style scoped>
.unauthorized {
  text-align: center;
  padding: 4rem 1rem;
}

.unauthorized h1 {
  font-size: 4rem;
  color: #e94560;
  margin-bottom: 0.5rem;
}

.btn {
  display: inline-block;
  margin-top: 1rem;
  padding: 0.5rem 1rem;
  background-color: #e94560;
  color: #fff;
  text-decoration: none;
  border-radius: 4px;
}
</style>
