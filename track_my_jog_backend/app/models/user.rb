class User < ActiveRecord::Base
  # Include default devise modules.
  devise :database_authenticatable, :registerable,
         :recoverable, :rememberable, :trackable, :validatable
  include DeviseTokenAuth::Concerns::User

  # Validations
  validates :email, :name, presence: true
  validates :role, inclusion: { in: %w(regular manager admin) }

  def can_create_role?(role)
    if self.role.to_sym == :admin
      true
    elsif self.role.to_sym == :manager
      role.to_sym == :manager || role.to_sym == :regular
    else
      role.to_sym == :regular
    end
  end
end
