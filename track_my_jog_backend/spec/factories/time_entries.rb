require 'faker'

FactoryGirl.define do
  factory :time_entry do
    date { Faker::Date.backward(15) }
    distance { Faker::Number.between(1, 10000) }
    duration { Faker::Number.between(1, 360) }
    user { association(:user) }
  end
end
