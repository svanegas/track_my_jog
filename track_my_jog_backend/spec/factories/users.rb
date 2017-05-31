require 'faker'

FactoryGirl.define do
  factory :user do
    name { Faker::Name.name }
    email { Faker::Internet.email(name) }
    password { Faker::Internet.password(8) }
    role :regular

    factory :manager_user do
      role :manager
    end

    factory :admin_user do
      role :admin
    end
  end
end
