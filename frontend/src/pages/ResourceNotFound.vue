<template>
  <div class="not-found">
    <h1>404</h1>
    <p>The page you're looking for doesn't exist.</p>
    <router-link to="/courses" class="btn">Back to Courses</router-link>
  </div>
</template>

<style scoped>
.not-found {
  text-align: center;
  padding: 4rem 1rem;
}

.not-found h1 {
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
