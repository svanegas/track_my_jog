# This file should contain all the record creation needed to seed the database with its default values.
# The data can then be loaded with the rails db:seed command (or created alongside the database with db:setup).
#
# Examples:
#
#   movies = Movie.create([{ name: 'Star Wars' }, { name: 'Lord of the Rings' }])
#   Character.create(name: 'Luke', movie: movies.first)

admin = FactoryGirl.create(:admin_user, email: 'admin@example.com', password: 'password')

3.times do
  user = FactoryGirl.create(:manager_user, password: 'password')
  rand(0, 5).times do
    FactoryGirl.create(:time_entry, user: user)
  end
end

5.times do
  user = FactoryGirl.create(:user, password: 'password')
  rand(0, 5).times do
    FactoryGirl.create(:time_entry, user: user)
  end
end
